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

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.internal.admin.configurator.opfactory.AdminOpFactory
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory
import org.codice.ddf.internal.admin.configurator.opfactory.ServiceReader
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.PID
import static org.codice.ddf.admin.sources.SourceTestCommons.S_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.configToBeDeleted

class DeleteWfsConfigurationTest extends Specification {

    Action deleteWfsConfiguration

    ConfiguratorFactory configuratorFactory

    ServiceReader serviceReader

    AdminOpFactory adminOpFactory

    ManagedServiceOpFactory managedServiceOpFactory

    Configurator configurator

    static RESULT_ARGUMENT_PATH = [DeleteWfsConfiguration.ID]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, BaseAction.ARGUMENT].flatten()

    static PID_PATH = [BASE_PATH, PID].flatten()

    def actionArgs = [
        (PID): S_PID
    ]

    def setup() {
        serviceReader = Mock(ServiceReader)
        adminOpFactory = Mock(AdminOpFactory)
        managedServiceOpFactory = Mock(ManagedServiceOpFactory)
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigurator() >> getConfigurator()
        }
        deleteWfsConfiguration = new DeleteWfsConfiguration(configuratorFactory, managedServiceOpFactory, adminOpFactory)
    }

    def 'Successfully delete WFS configuration'() {
        setup:
        adminOpFactory.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(false)
        deleteWfsConfiguration.setArguments(actionArgs)

        when:
        def report = deleteWfsConfiguration.process()

        then:
        report.result() != null
        report.result().getValue() == true
    }

    def 'Fail to discover WFS config when no existing config found with provided pid'() {
        setup:
        adminOpFactory.read(S_PID) >> [:]
        deleteWfsConfiguration.setArguments(actionArgs)

        when:
        def report = deleteWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Error while committing delete configuration with given pid'() {
        when:
        adminOpFactory.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteWfsConfiguration.setArguments(actionArgs)
        def report = deleteWfsConfiguration.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_DELETE_ERROR
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = deleteWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [PID_PATH]
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
