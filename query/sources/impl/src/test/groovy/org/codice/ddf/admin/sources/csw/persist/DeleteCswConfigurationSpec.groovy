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

import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.services.CswServiceProperties
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions

class DeleteCswConfigurationSpec extends SourceCommonsSpec {
    static final List<Object> FUNCTION_PATH = [DeleteCswConfiguration.FIELD_NAME]

    DeleteCswConfiguration deleteCswConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ServiceActions serviceActions

    ConfiguratorSuite configuratorSuite

    static TEST_CSW_URL = "testCswUrl"

    static EVENT_SERVICE_ADDRESS = "eventServiceAddress"

    static TEST_EVENT_SERVICE_ADDRESS = "testEventServiceAddress"

    static RESULT_ARGUMENT_PATH = [DeleteCswConfiguration.FIELD_NAME]

    static BASE_PATH = [DeleteCswConfiguration.FIELD_NAME]

    static SERVICE_PID_PATH = [BASE_PATH, PID].flatten()

    def args = [
            (PID): S_PID
    ]

    def configToDelete = createCswConfigToDelete()

    def serviceCommons

    def setup() {
        configurator = Mock(Configurator)
        serviceActions = Mock(ServiceActions)
        def managedServiceActions = Mock(ManagedServiceActions)

        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigurator() >> configurator
        }

        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.managedServiceActions >> managedServiceActions

        serviceCommons = new ServiceCommons(configuratorSuite)

        deleteCswConfiguration = new DeleteCswConfiguration(serviceCommons)
    }

    def 'Successfully deleting CSW configuration returns true'() {
        when:
        serviceActions.read(S_PID) >> configToDelete
        configurator.commit(_, _) >> mockReport(false)
        def report = deleteCswConfiguration.execute(args, FUNCTION_PATH)

        then:
        report.getResult() != null
        report.getResult().getValue()
    }

    def 'Fail with no existing config found with provided pid'() {
        when:
        serviceActions.read(_ as String) >> [:]
        def report = deleteCswConfiguration.execute(args, FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
    }

    def 'Error while committing deleted configuration with the given servicePid'() {
        when:
        serviceActions.read(S_PID) >> configToDelete
        configurator.commit(_, _) >> mockReport(true)
        def report = deleteCswConfiguration.execute(args, FUNCTION_PATH)

        then:
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Fail when missing required fields'() {
        when:
        def report = deleteCswConfiguration.execute(null, FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.getErrorMessages()*.getPath() == [SERVICE_PID_PATH]
    }

    def 'Returns all the possible error codes correctly'() {
        setup:
        DeleteCswConfiguration deleteCswNoExistingConfig = new DeleteCswConfiguration(serviceCommons)
        serviceActions.read(_ as String) >> [:]

        DeleteCswConfiguration deleteCswFailPersist = new DeleteCswConfiguration(serviceCommons)
        serviceActions.read(S_PID) >> configToDelete
        configurator.commit(_, _) >> mockReport(true)

        when:
        def errorCodes = deleteCswConfiguration.getFunctionErrorCodes()
        def noExistingConfigReport = deleteCswNoExistingConfig.execute(args, FUNCTION_PATH)
        def failedPersistReport = deleteCswFailPersist.execute(args, FUNCTION_PATH)

        then:
        errorCodes.size() == 2
        errorCodes.contains(noExistingConfigReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(failedPersistReport.getErrorMessages().get(0).getCode())
    }

    def createCswConfigToDelete() {
        configToDelete = configToBeDeleted
        configToDelete.put(EVENT_SERVICE_ADDRESS, TEST_EVENT_SERVICE_ADDRESS)
        configToDelete.put(CswServiceProperties.CSW_URL, TEST_CSW_URL)
        return configToDelete;
    }
}
