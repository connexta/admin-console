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
package org.codice.ddf.admin.sources.opensearch.persist

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
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class SaveOpenSearchConfigurationTest extends Specification {

    static BASE_PATH = [SaveOpenSearchConfiguration.ID, BaseAction.ARGUMENT]

    static CONFIG_PATH = [BASE_PATH, SOURCE_CONFIG].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SOURCE_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static SERVICE_PID_PATH = [BASE_PATH, SERVICE_PID].flatten()

    Action saveOpenSearchConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ConfigReader configReader

    FederatedSource federatedSource

    def federatedSources = []

    def setup() {
        refreshSaveConfigActionArgs()
        configReader = Mock(ConfigReader)
        configurator = Mock(Configurator)
        federatedSource = Mock(FederatedSource)
        federatedSource.getId() >> TEST_SOURCENAME
        federatedSources.add(federatedSource)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
            getConfigurator() >> configurator
        }
        saveOpenSearchConfiguration = new SaveOpenSearchConfiguration(configuratorFactory)
    }

    def 'test new configuration save successful'() {
        setup:
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() != null
        assertConfig(report.result(), SaveOpenSearchConfiguration.ID, saveConfigActionArgs.get(SOURCE_CONFIG))
    }

    def 'test fail to save new config due to duplicate source name'() {
        setup:
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)
        configReader.getServices(_, _) >> federatedSources

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
        report.messages().get(0).path == SOURCE_NAME_PATH
    }

    def 'test fail to save new config due to failure to commit'() {
        setup:
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == CONFIG_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'test update configuration successful'() {
        setup:
        saveConfigActionArgs.put(SERVICE_PID, S_PID)
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)
        configReader.getConfig(_) >> [(ID):TEST_SOURCENAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() != null
        assertConfig(report.result(), SaveOpenSearchConfiguration.ID, saveConfigActionArgs.get(SOURCE_CONFIG))
    }

    def 'test fail update config due to existing source name'() {
        setup:
        saveConfigActionArgs.put(SERVICE_PID, S_PID)
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)
        configReader.getConfig(_) >> [(ID):'someOtherSourceName']
        configReader.getServices(_, _) >> federatedSources

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'test fail to update config due to failure to commit'() {
        setup:
        saveConfigActionArgs.put(SERVICE_PID, S_PID)
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)
        configReader.getConfig(_) >> [(ID):TEST_SOURCENAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_UPDATE_ERROR
    }

    def 'test fail update config due to no existing source'() {
        setup:
        saveConfigActionArgs.put(SERVICE_PID, S_PID)
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)
        configReader.getConfig(S_PID) >> [:]

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def 'test fail update due to empty service pid'() {
        setup:
        saveConfigActionArgs.put(SERVICE_PID, '')
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
    }

    def 'test fail save due to missing required sourcename field'() {
        setup:
        saveConfigActionArgs.get(SOURCE_CONFIG).put(SOURCE_NAME, null)
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def 'test fail save due to missing required endpoint url field'() {
        setup:
        saveConfigActionArgs.get(SOURCE_CONFIG).put(ENDPOINT_URL, null)
        saveOpenSearchConfiguration.setArguments(saveConfigActionArgs)

        when:
        def report = saveOpenSearchConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == ENDPOINT_URL_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def assertConfig(Field field, String actionId, Map<String, Object> properties) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.isAvailable()
        assert sourceInfo.sourceHandlerName() == actionId
        assert sourceInfo.config().endpointUrl() == properties.get(ENDPOINT_URL)
        assert sourceInfo.config().credentials().password() == "*****"
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == TEST_SOURCENAME
        assert sourceInfo.config().factoryPid() == F_PID
        return true
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
