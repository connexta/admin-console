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

import org.codice.ddf.admin.api.Field
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField
import org.codice.ddf.admin.sources.services.WfsServiceProperties
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class GetWfsConfigsTest extends Specification {

    GetWfsConfigurations getWfsConfigsFunction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    static TEST_WFS_VERSION_1 = WfsVersion.WFS_VERSION_1

    static TEST_WFS_VERSION_2 = WfsVersion.WFS_VERSION_2

    static TEST_FACTORY_PID_1 = WfsServiceProperties.WFS1_FACTORY_PID

    static TEST_FACTORY_PID_2 = WfsServiceProperties.WFS2_FACTORY_PID

    static RESULT_ARGUMENT_PATH = [GetWfsConfigurations.ID]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    def managedServiceConfigs

    def functionArgs = [
        (PID): S_PID_2
    ]

    def setup() {
        managedServiceConfigs = createWfsManagedServiceConfigs()
        configReader = Mock(ConfigReader)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
        }
        getWfsConfigsFunction = new GetWfsConfigurations(configuratorFactory)
    }

    def 'No pid argument returns all configs'() {
        setup:
        configReader.getServices(_, _) >> []

        when:
        def report = getWfsConfigsFunction.getValue()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getManagedServiceConfigs(_ as String) >> managedServiceConfigs
        1 * configReader.getManagedServiceConfigs(_ as String) >> [:]
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
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getServices(_, _) >> []
        configReader.getConfig(S_PID_2) >> managedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, SOURCE_ID_2, S_PID_2, false, TEST_WFS_VERSION_2)
    }

    def 'Fail due to no existing config with specified pid'() {
        setup:
        functionArgs.put(PID, S_PID)
        getWfsConfigsFunction.setValue(functionArgs)
        configReader.getConfig(S_PID) >> [:]

        when:
        def report = getWfsConfigsFunction.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def assertConfig(Field field, int index, String sourceName, String pid, boolean availability, String wfsVersion) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.fieldName() == ListFieldImpl.INDEX_DELIMETER + index
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.config().credentials().password() == FLAG_PASSWORD
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().pid() == pid
        assert ((WfsSourceConfigurationField)sourceInfo.config()).wfsVersion() == wfsVersion
        return true
    }

    def createWfsManagedServiceConfigs() {
        managedServiceConfigs = baseManagedServiceConfigs
        managedServiceConfigs.get(S_PID_1).put(FACTORY_PID_KEY, TEST_FACTORY_PID_1)
        managedServiceConfigs.get(S_PID_2).put(FACTORY_PID_KEY, TEST_FACTORY_PID_2)
        return managedServiceConfigs
    }
}
