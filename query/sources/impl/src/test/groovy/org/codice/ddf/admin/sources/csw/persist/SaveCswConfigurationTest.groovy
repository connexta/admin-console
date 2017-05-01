/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.sources.csw.persist

import ddf.catalog.source.FederatedSource
import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.commons.SourceMessages
import org.codice.ddf.admin.sources.fields.ServicePid
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

class SaveCswConfigurationTest extends Specification {

    static TEST_SPATIAL_FILTER = 'testForcedSpatialFeature'

    static TEST_OUTPUT_SCHEMA = 'testOutputSchema'

    static TEST_SOURCE_NAME = 'testSourceName'

    static TEST_SERVICE_PID = 'testServicePid'

    static CONFIG_PATH = [SaveCswConfiguration.ID, BaseAction.ARGUMENT, SourceConfigUnionField.FIELD_NAME]

    static ENDPOINT_URL_PATH = [CONFIG_PATH, SourceConfigUnionField.ENDPOINT_URL_FIELD].flatten()

    static SERVICE_PID_PATH = [SaveCswConfiguration.ID, BaseAction.ARGUMENT, ServicePid.DEFAULT_FIELD_NAME]

    static SOURCE_NAME_PATH = [CONFIG_PATH, SourceConfigUnionField.SOURCE_NAME_FIELD].flatten()

    Action saveCswConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ConfigReader configReader

    FederatedSource federatedSource

    def actionArgs

    def federatedSources = []

    def setup() {
        refreshActionArgs()

        configurator = Mock(Configurator)
        configReader = Mock(ConfigReader)

        federatedSource = Mock(FederatedSource)
        federatedSource.getId() >> TEST_SOURCE_NAME

        configuratorFactory = Mock(ConfiguratorFactory)
        configuratorFactory.getConfigurator() >> configurator
        configuratorFactory.getConfigReader() >> configReader

        saveCswConfiguration = new SaveCswConfiguration(configuratorFactory)
        federatedSources.add(federatedSource)
    }

    def 'test new configuration save successful'() {
        when:
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)
        def report = saveCswConfiguration.process()

        then:
        report.result() != null
        assertConfig(report.result(), SaveCswConfiguration.ID, actionArgs.get(SourceConfigUnionField.FIELD_NAME), null)
    }

    def 'test fail to save new config due to duplicate source name'() {
        when:
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getServices(_, _) >> federatedSources
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'test fail to save new config due to failure to commit'() {
        when:
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == CONFIG_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'test update configuration successful'() {
        setup:
        actionArgs.put(ServicePid.DEFAULT_FIELD_NAME, TEST_SERVICE_PID)
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [id:TEST_SOURCE_NAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() != null
        assertConfig(report.result(), SaveCswConfiguration.ID, actionArgs.get(SourceConfigUnionField.FIELD_NAME), null)
    }

    def 'test fail update config due to existing source name'() {
        setup:
        actionArgs.put(ServicePid.DEFAULT_FIELD_NAME, TEST_SERVICE_PID)
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [id:'someOtherSourceName']
        configReader.getServices(_, _) >> federatedSources

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'test fail to update config due to failure to commit'() {
        setup:
        actionArgs.put(ServicePid.DEFAULT_FIELD_NAME, TEST_SERVICE_PID)
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [id:TEST_SOURCE_NAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_UPDATE_ERROR
    }

    def 'test fail to update config due to no existing source'() {
        setup:
        actionArgs.put(ServicePid.DEFAULT_FIELD_NAME, TEST_SERVICE_PID)
        saveCswConfiguration.setArguments(actionArgs);
        configReader.getConfig(TEST_SERVICE_PID) >> [:]

        when:
        def report = saveCswConfiguration.process()

        then:
        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
    }

    def 'test fail update due to empty servicePid'() {
        setup:
        actionArgs.put(ServicePid.DEFAULT_FIELD_NAME, "")
        saveCswConfiguration.setArguments(actionArgs);

        when:
        def report = saveCswConfiguration.process()

        then:
        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
    }

    def 'test fail save due to missing required source name field'() {
        setup:
        actionArgs.get(SourceConfigUnionField.FIELD_NAME).put(SourceConfigUnionField.SOURCE_NAME_FIELD, null)
        saveCswConfiguration.setArguments(actionArgs);

        when:
        def report = saveCswConfiguration.process()

        then:
        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def 'test fail save due to missing required endpoint url field'() {
        setup:
        actionArgs.get(SourceConfigUnionField.FIELD_NAME).put(SourceConfigUnionField.ENDPOINT_URL_FIELD, null)
        saveCswConfiguration.setArguments(actionArgs);

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == ENDPOINT_URL_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def assertConfig(Field field, String actionId, Map<String, Object> properties, String servicePid) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.isAvailable()
        assert sourceInfo.sourceHandlerName() == actionId
        assert sourceInfo.config().endpointUrl() == properties.get((SourceConfigUnionField.ENDPOINT_URL_FIELD))
        assert sourceInfo.config().credentials().password() == "*****"
        assert sourceInfo.config().credentials().username() == "admin"
        assert sourceInfo.config().sourceName() == TEST_SOURCE_NAME
        assert sourceInfo.config().factoryPid() == "testFactoryPid"
        assert ((CswSourceConfigurationField)sourceInfo.config()).forceSpatialFilter() == TEST_SPATIAL_FILTER
        assert ((CswSourceConfigurationField)sourceInfo.config()).outputSchema() == TEST_OUTPUT_SCHEMA
        if(servicePid != null) {
            assert sourceInfo.config().servicePid() == servicePid
        }
        return true
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }

    def refreshActionArgs() {
        actionArgs = [
            (SourceConfigUnionField.FIELD_NAME): [
                (SourceConfigUnionField.ENDPOINT_URL_FIELD)        : "https://localhost:8993",
                (SourceConfigUnionField.FACTORY_PID_FIELD)         : "testFactoryPid",
                sourceName                                         : TEST_SOURCE_NAME,
                (CredentialsField.DEFAULT_FIELD_NAME)              : [
                    username: "admin",
                    password: "admin"
                ],
                (CswSourceConfigurationField.FORCED_SPATIAL_FILTER): TEST_SPATIAL_FILTER,
                (CswSourceConfigurationField.OUTPUT_SCHEMA)        : TEST_OUTPUT_SCHEMA
            ]
        ]
    }
}
