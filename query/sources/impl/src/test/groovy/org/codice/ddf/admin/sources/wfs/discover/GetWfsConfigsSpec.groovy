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
package org.codice.ddf.admin.sources.wfs.discover

import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.Field
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.services.WfsServiceProperties
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.admin.sources.wfs.WfsSourceInfoField
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader

class GetWfsConfigsSpec extends SourceCommonsSpec {

    GetWfsConfigurations getWfsConfigsFunction

    ConfiguratorFactory configuratorFactory

    private ServiceActions serviceActions

    private ManagedServiceActions managedServiceActions

    private ServiceReader serviceReader

    static TEST_WFS_VERSION_1 = WfsVersion.Wfs1.WFS_VERSION_1

    static TEST_WFS_VERSION_2 = WfsVersion.Wfs2.WFS_VERSION_2

    static TEST_FACTORY_PID_1 = WfsServiceProperties.WFS1_FACTORY_PID

    static TEST_FACTORY_PID_2 = WfsServiceProperties.WFS2_FACTORY_PID

    static RESULT_ARGUMENT_PATH = [GetWfsConfigurations.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    def managedServiceConfigs

    def functionArgs = [
            (PID): S_PID_2
    ]

    def setup() {
        managedServiceConfigs = createWfsManagedServiceConfigs()
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        managedServiceActions = Mock(ManagedServiceActions)
        serviceReader = Mock(ServiceReader)
        def configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.serviceReader >> serviceReader
        configuratorSuite.managedServiceActions >> managedServiceActions

        getWfsConfigsFunction = new GetWfsConfigurations(configuratorSuite)
    }

    def 'No pid argument returns all configs'() {
        setup:
        serviceReader.getServices(_, _) >> []

        when:
        def report = getWfsConfigsFunction.getValue()
        def list = ((ListField) report.result())

        then:
        1 * managedServiceActions.read(_ as String) >> managedServiceConfigs
        1 * managedServiceActions.read(_ as String) >> [:]
        2 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        2 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        report.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, SOURCE_ID_1, S_PID_1, true, TEST_WFS_VERSION_1)
        assertConfig(list.getList().get(1), 1, SOURCE_ID_2, S_PID_2, false, TEST_WFS_VERSION_2)
    }

    def 'Pid filter returns 1 result'() {
        setup:
        getWfsConfigsFunction.setValue(functionArgs)

        when:
        def report = getWfsConfigsFunction.getValue()
        def list = ((ListField) report.result())

        then:
        1 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * serviceReader.getServices(_, _) >> []
        serviceActions.read(S_PID_2) >> managedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, SOURCE_ID_2, S_PID_2, false, TEST_WFS_VERSION_2)
    }

    def 'Fail due to no existing config with specified pid'() {
        setup:
        functionArgs.put(PID, S_PID)
        getWfsConfigsFunction.setValue(functionArgs)
        serviceActions.read(S_PID) >> [:]

        when:
        def report = getWfsConfigsFunction.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def assertConfig(Field field, int index, String sourceName, String pid, boolean availability, String wfsVersion) {
        def sourceInfo = (WfsSourceInfoField) field
        assert sourceInfo.path()[-1] == index.toString()
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.config().credentials().password() == FLAG_PASSWORD
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().pid() == pid
        assert sourceInfo.config().wfsVersion() == wfsVersion
        return true
    }

    def createWfsManagedServiceConfigs() {
        managedServiceConfigs = baseManagedServiceConfigs
        managedServiceConfigs.get(S_PID_1).put(FACTORY_PID_KEY, TEST_FACTORY_PID_1)
        managedServiceConfigs.get(S_PID_2).put(FACTORY_PID_KEY, TEST_FACTORY_PID_2)
        return managedServiceConfigs
    }
}
