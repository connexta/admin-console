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
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.SERVICE_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.S_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.configToBeDeleted

class DeleteWfsConfigurationTest extends Specification {

    Action deleteWfsConfiguration

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    Configurator configurator

    static BASE_PATH = [DeleteWfsConfiguration.ID, BaseAction.ARGUMENT]

    static SERVICE_PID_PATH = [BASE_PATH, SERVICE_PID].flatten()

    def actionArgs = [
        (SERVICE_PID) : S_PID
    ]

    def setup() {
        configReader = Mock(ConfigReader)
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
            getConfigurator() >> getConfigurator()
        }
        deleteWfsConfiguration = new DeleteWfsConfiguration(configuratorFactory)
    }

    def 'test success delete config returns true'() {
        setup:
        configReader.getConfig(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(false)
        deleteWfsConfiguration.setArguments(actionArgs)

        when:
        def report = deleteWfsConfiguration.process()

        then:
        report.result() != null
        report.result().getValue() == true
    }

    def 'test no config found with provided service pid'() {
        setup:
        configReader.getConfig(S_PID) >> [:]
        deleteWfsConfiguration.setArguments(actionArgs)

        when:
        def report = deleteWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def 'test error while committing delete configuration with given service pid'() {
        when:
        configReader.getConfig(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteWfsConfiguration.setArguments(actionArgs)
        def report = deleteWfsConfiguration.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_DELETE_ERROR
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def 'test failure due to required service pid argument not provided'() {
        when:
        def report = deleteWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def 'test failure due to service pid argument provided but empty'() {
        when:
        deleteWfsConfiguration.setArguments([(SERVICE_PID):''])
        def report = deleteWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
