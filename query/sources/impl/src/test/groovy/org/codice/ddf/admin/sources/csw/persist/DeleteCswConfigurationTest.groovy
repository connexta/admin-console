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

import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.services.CswServiceProperties
import spock.lang.Ignore
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DeleteCswConfigurationTest extends Specification {

    DeleteCswConfiguration deleteCswConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    static TEST_CSW_URL = "testCswUrl"

    static EVENT_SERVICE_ADDRESS = "eventServiceAddress"

    static TEST_EVENT_SERVICE_ADDRESS = "testEventServiceAddress"

    static RESULT_ARGUMENT_PATH = [DeleteCswConfiguration.ID]

    static BASE_PATH = [DeleteCswConfiguration.ID, FunctionField.ARGUMENT]

    static SERVICE_PID_PATH = [BASE_PATH, PID].flatten()

    def functionArgs = [
        (PID): S_PID
    ]

    def configToDelete = createCswConfigToDelete()

    def setup() {
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigurator() >> configurator
        }
        deleteCswConfiguration = new DeleteCswConfiguration(configuratorFactory, adminActions, managedServiceActions)
    }

    @Ignore
    def 'Successfully deleting CSW configuration returns true'() {
        when:
        configReader.getConfig(S_PID) >> configToDelete
        configurator.commit(_, _) >> mockReport(false)
        deleteCswConfiguration.setValue(functionArgs)
        def report = deleteCswConfiguration.getValue()

        then:
        report.result() != null
        report.result().getValue() == true
    }

    @Ignore
    def 'Fail with no existing config found with provided pid'() {
        when:
        configReader.getConfig(_ as String) >> [:]
        deleteCswConfiguration.setValue(functionArgs)
        def report = deleteCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
    }

    @Ignore
    def 'Error while committing deleted configuration with the given servicePid'() {
        when:
        configReader.getConfig(S_PID) >> configToDelete
        configurator.commit(_, _) >> mockReport(true)
        deleteCswConfiguration.setValue(functionArgs)
        def report = deleteCswConfiguration.getValue()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_DELETE_ERROR
    }

    @Ignore
    def 'Fail when missing required fields'() {
        when:
        def report = deleteCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [SERVICE_PID_PATH]
    }

    private def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }

    private def createCswConfigToDelete() {
        configToDelete = configToBeDeleted
        configToDelete.put(EVENT_SERVICE_ADDRESS, TEST_EVENT_SERVICE_ADDRESS)
        configToDelete.put(CswServiceProperties.CSW_URL, TEST_CSW_URL)
        return configToDelete;
    }
}
