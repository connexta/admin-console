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
package org.codice.ddf.admin.sources.opensearch.discover

import org.codice.ddf.admin.api.Field
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceInfoField
import org.codice.ddf.admin.sources.services.OpenSearchServiceProperties
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class GetOpenSearchConfigsTest extends Specification {

    static SHORT_NAME = OpenSearchServiceProperties.SHORTNAME

    static TEST_SHORT_NAME = "openSearchSource"

    static RESULT_ARGUMENT_PATH = [GetOpenSearchConfigurations.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    GetOpenSearchConfigurations getOpenSearchConfigsFunction

    ConfiguratorFactory configuratorFactory

    ManagedServiceActions managedServiceActions

    ServiceActions serviceActions

    ServiceReader serviceReader

    def managedServiceConfigs

    def functionArgs = [
            (PID): S_PID_2
    ]

    def setup() {
        managedServiceConfigs = createOpenSearchManagedServiceConfigs()
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        serviceReader = Mock(ServiceReader)
        managedServiceActions = Mock(ManagedServiceActions)
        getOpenSearchConfigsFunction = new GetOpenSearchConfigurations(configuratorFactory, serviceActions,
                this.managedServiceActions, serviceReader)
    }

    def 'No pid argument returns all configs'() {
        when:
        def report = getOpenSearchConfigsFunction.getValue()
        def list = ((ListField) report.result())

        then:
        1 * managedServiceActions.read(_ as String) >> baseManagedServiceConfigs
        2 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        2 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        report.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, TEST_SHORT_NAME, S_PID_1, true)
        assertConfig(list.getList().get(1), 1, TEST_SHORT_NAME, S_PID_2, false)
    }

    def 'Pid filter returns 1 result'() {
        setup:
        getOpenSearchConfigsFunction.setValue(functionArgs)

        when:
        def report = getOpenSearchConfigsFunction.getValue()
        def list = ((ListField) report.result())

        then:
        1 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * serviceReader.getServices(_, _) >> []
        serviceActions.read(S_PID_2) >> baseManagedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, TEST_SHORT_NAME, S_PID_2, false)
    }

    def 'Fail due to no existing config with specified pid'() {
        setup:
        functionArgs.put(PID, S_PID)
        getOpenSearchConfigsFunction.setValue(functionArgs)
        serviceActions.read(S_PID) >> [:]

        when:
        def report = getOpenSearchConfigsFunction.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def createOpenSearchManagedServiceConfigs() {
        managedServiceConfigs = baseManagedServiceConfigs
        managedServiceConfigs.get(S_PID_1).put(SHORT_NAME, TEST_SHORT_NAME)
        managedServiceConfigs.get(S_PID_2).put(SHORT_NAME, TEST_SHORT_NAME)
        return managedServiceConfigs
    }

    def assertConfig(Field field, int index, String sourceName, String pid, boolean availability) {
        def sourceInfo = (OpenSearchSourceInfoField) field
        assert sourceInfo.path()[-1] == index.toString()
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.config().credentials().password() == FLAG_PASSWORD
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().pid() == pid
        return true
    }
}
