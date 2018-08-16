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
 **/
package org.codice.ddf.admin.sources.wfs.persist

import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions

class DeleteWfsConfigurationSpec extends SourceCommonsSpec {

    static final List<Object> FUNCTION_PATH = [DeleteWfsConfiguration.FIELD_NAME]

    DeleteWfsConfiguration deleteWfsConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ConfiguratorSuite configuratorSuite

    private ServiceActions serviceActions

    ServiceCommons serviceCommons

    static RESULT_ARGUMENT_PATH = [DeleteWfsConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH].flatten()

    static PID_PATH = [BASE_PATH, PID].flatten()

    def functionArgs = [
            (PID): S_PID
    ]

    def setup() {
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigurator() >> getConfigurator()
        }
        serviceActions = Mock(ServiceActions)
        def managedServiceActions = Mock(ManagedServiceActions)

        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.managedServiceActions >> managedServiceActions

        serviceCommons = new ServiceCommons(configuratorSuite)

        deleteWfsConfiguration = new DeleteWfsConfiguration(serviceCommons)
    }

    def 'Successfully delete WFS configuration'() {
        setup:
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = deleteWfsConfiguration.execute(functionArgs, FUNCTION_PATH)

        then:
        report.getResult() != null
        report.getResult().getValue()
    }

    def 'Fail to discover WFS config when no existing config found with provided pid'() {
        setup:
        serviceActions.read(S_PID) >> [:]

        when:
        def report = deleteWfsConfiguration.execute(functionArgs, FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Error while committing delete configuration with given pid'() {
        when:
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        def report = deleteWfsConfiguration.execute(functionArgs, FUNCTION_PATH)

        then:
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = deleteWfsConfiguration.execute(null, FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.getErrorMessages()*.getPath() == [PID_PATH]
    }

    def 'Returns all the possible error codes correctly'() {
        setup:
        DeleteWfsConfiguration deleteWfsNoExistingConfig = new DeleteWfsConfiguration(serviceCommons)
        serviceActions.read(S_PID) >> [:]

        DeleteWfsConfiguration deleteWfsFailPersist = new DeleteWfsConfiguration(serviceCommons)
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)

        when:
        def errorCodes = deleteWfsConfiguration.getFunctionErrorCodes()
        def noExistingConfigReport = deleteWfsNoExistingConfig.execute(functionArgs, FUNCTION_PATH)
        def failedPersistReport = deleteWfsFailPersist.execute(functionArgs, FUNCTION_PATH)

        then:
        errorCodes.size() == 2
        errorCodes.contains(noExistingConfigReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(failedPersistReport.getErrorMessages().get(0).getCode())
    }
}
