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

import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions

class DeleteWfsConfigurationSpec extends SourceCommonsSpec {

    DeleteWfsConfiguration deleteWfsConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    private ServiceActions serviceActions

    static RESULT_ARGUMENT_PATH = [DeleteWfsConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

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

        deleteWfsConfiguration = new DeleteWfsConfiguration(configuratorFactory, serviceActions,
                managedServiceActions)
    }

    def 'Successfully delete WFS configuration'() {
        setup:
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(false)
        deleteWfsConfiguration.setValue(functionArgs)

        when:
        def report = deleteWfsConfiguration.getValue()

        then:
        report.result() != null
        report.result().getValue()
    }

    def 'Fail to discover WFS config when no existing config found with provided pid'() {
        setup:
        serviceActions.read(S_PID) >> [:]
        deleteWfsConfiguration.setValue(functionArgs)

        when:
        def report = deleteWfsConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Error while committing delete configuration with given pid'() {
        when:
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteWfsConfiguration.setValue(functionArgs)
        def report = deleteWfsConfiguration.getValue()

        then:
        !report.result().getValue()
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = deleteWfsConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [PID_PATH]
    }

    private def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
