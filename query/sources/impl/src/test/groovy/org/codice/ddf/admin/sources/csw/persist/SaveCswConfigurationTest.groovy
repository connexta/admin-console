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
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.commons.SourceMessages
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class SaveCswConfigurationTest extends Specification {

    static TEST_SPATIAL_FILTER = 'testForcedSpatialFeature'

    static TEST_OUTPUT_SCHEMA = 'testOutputSchema'

    static BASE_PATH = [SaveCswConfiguration.ID, BaseAction.ARGUMENT]

    static CONFIG_PATH = [BASE_PATH, SOURCE_CONFIG].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static SERVICE_PID_PATH = [BASE_PATH, SERVICE_PID].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SOURCE_NAME].flatten()

    Action saveCswConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ConfigReader configReader

    FederatedSource federatedSource

    def actionArgs

    def federatedSources = []

    def setup() {
        actionArgs = createCswSaveArgs()
        configurator = Mock(Configurator)
        configReader = Mock(ConfigReader)
        federatedSource = Mock(FederatedSource)
        configuratorFactory = Mock(ConfiguratorFactory)
        federatedSource.getId() >> TEST_SOURCENAME
        federatedSources.add(federatedSource)
        configuratorFactory.getConfigurator() >> configurator
        configuratorFactory.getConfigReader() >> configReader
        saveCswConfiguration = new SaveCswConfiguration(configuratorFactory)
    }

    def 'test new configuration save successful'() {
        when:
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)
        def report = saveCswConfiguration.process()

        then:
        report.result() != null
        report.result().getValue() == true
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
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).path == CONFIG_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'test update configuration successful'() {
        setup:
        actionArgs.put(SERVICE_PID, S_PID)
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [(ID):TEST_SOURCENAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() != null
        report.result().getValue() == true
    }

    def 'test fail update config due to existing source name'() {
        setup:
        actionArgs.put(SERVICE_PID, S_PID)
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [(ID):'someOtherSourceName']
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
        actionArgs.put(SERVICE_PID, S_PID)
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [(ID):TEST_SOURCENAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_UPDATE_ERROR
    }

    def 'test fail to update config due to no existing source'() {
        setup:
        actionArgs.put(SERVICE_PID, S_PID)
        saveCswConfiguration.setArguments(actionArgs)
        configReader.getConfig(S_PID) >> [:]

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
    }

    def 'test fail update due to empty servicePid'() {
        setup:
        actionArgs.put(SERVICE_PID, '')
        saveCswConfiguration.setArguments(actionArgs)

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
    }

    def 'test fail save due to missing required source name field'() {
        setup:
        actionArgs.get(SOURCE_CONFIG).put(SOURCE_NAME, null)
        saveCswConfiguration.setArguments(actionArgs)

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def 'test fail save due to missing required endpoint url field'() {
        setup:
        actionArgs.get(SOURCE_CONFIG).put(ENDPOINT_URL, null)
        saveCswConfiguration.setArguments(actionArgs)

        when:
        def report = saveCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == ENDPOINT_URL_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }


    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }

    def createCswSaveArgs() {
        refreshSaveConfigActionArgs()
        actionArgs = saveConfigActionArgs
        actionArgs.get(SOURCE_CONFIG).put(CswSourceConfigurationField.FORCED_SPATIAL_FILTER, TEST_SPATIAL_FILTER)
        actionArgs.get(SOURCE_CONFIG).put(CswSourceConfigurationField.OUTPUT_SCHEMA, TEST_OUTPUT_SCHEMA)
        return actionArgs
    }
}
