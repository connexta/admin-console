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

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.services.CswServiceProperties
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.F_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.SERVICE_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.SOURCE_ID_1
import static org.codice.ddf.admin.sources.SourceTestCommons.S_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.TEST_USERNAME
import static org.codice.ddf.admin.sources.SourceTestCommons.configToBeDeleted

class DeleteCswConfigurationTest extends Specification {

    Action deleteCswConfiguration

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    Configurator configurator

    static TEST_CSW_URL = "testCswUrl"

    static EVENT_SERVICE_ADDRESS = "eventServiceAddress"

    static TEST_EVENT_SERVICE_ADDRESS = "testEventServiceAddress"

    static BASE_PATH = [DeleteCswConfiguration.ID, BaseAction.ARGUMENT]

    static SERVICE_PID_PATH = [BASE_PATH, SERVICE_PID].flatten()

    def actionArgs = [
        (SERVICE_PID) : S_PID
    ]

    def configToDelete = createCswConfigToDelete()

    def setup() {
        configReader = Mock(ConfigReader)
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
            getConfigurator() >> configurator
        }
        deleteCswConfiguration = new DeleteCswConfiguration(configuratorFactory)
    }

    def 'test success delete config returns true'() {
        when:
        configReader.getConfig(S_PID) >> configToDelete
        configurator.commit(_, _) >> mockReport(false)
        deleteCswConfiguration.setArguments(actionArgs)
        def report = deleteCswConfiguration.process()

        then:
        report.result() != null
        report.result().getValue() == true
    }

    def 'test no config found with provided servicePid'() {
        when:
        configReader.getConfig(_ as String) >> [:]
        deleteCswConfiguration.setArguments(actionArgs)
        def report = deleteCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
    }

    def 'test error while committing deleted configuration with the given servicePid'() {
        when:
        configReader.getConfig(S_PID) >> configToDelete
        configurator.commit(_, _) >> mockReport(true)
        deleteCswConfiguration.setArguments(actionArgs)
        def report = deleteCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_DELETE_ERROR
    }

    def 'test failure due to required servicePid argument not provided'() {
        when:
        def report = deleteCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def 'test failure due to service pid argument provided but empty'() {
        when:
        deleteCswConfiguration.setArguments([(SERVICE_PID) : ""])
        def report = deleteCswConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SERVICE_PID_PATH
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
    }

    def assertConfig(Field field, String actionId, Map<String, Object> properties, String servicePid) {
        def sourceInfo = (SourceInfoField) field
        assert !sourceInfo.isAvailable()
        assert sourceInfo.sourceHandlerName() == actionId
        assert sourceInfo.config().endpointUrl() == properties.get(CswServiceProperties.CSW_URL)
        assert sourceInfo.config().credentials().password() == "*****"
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == SOURCE_ID_1
        assert sourceInfo.config().factoryPid() == F_PID
        assert sourceInfo.config().servicePid() == servicePid
        return true
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }

    def createCswConfigToDelete() {
        configToDelete = configToBeDeleted
        configToDelete.put(EVENT_SERVICE_ADDRESS, TEST_EVENT_SERVICE_ADDRESS)
        configToDelete.put(CswServiceProperties.CSW_URL, TEST_CSW_URL)
        return configToDelete;
    }
}
