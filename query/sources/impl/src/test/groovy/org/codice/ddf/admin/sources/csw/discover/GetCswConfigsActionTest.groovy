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
package org.codice.ddf.admin.sources.csw.discover

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.services.CswServiceProperties
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class GetCswConfigsActionTest extends Specification {

    static EVENT_SERVICE_ADDRESS = 'eventServiceAddress'

    static TEST_URL = "testCswUrl"

    static TEST_EVENT_SERVICE_ADDRESS = 'testEventServiceAddress'

    static BASE_PATH = [GetCswConfigsAction.ID, BaseAction.ARGUMENT]

    static SERVICE_PID_PATH = [BASE_PATH, SERVICE_PID].flatten()

    Action getCswConfigsAction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    def actionArgs = [
        (SERVICE_PID) : S_PID_2
    ]

    Map<String, Map<String, Object>> managedServiceConfigs = createCswManagedServiceConfigs()

    def setup() {
        configReader = Mock(ConfigReader)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
        }

        getCswConfigsAction = new GetCswConfigsAction(configuratorFactory)
    }

    def 'test no servicePid argument returns all configs'() {
        when:
        def report = getCswConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getManagedServiceConfigs(CswServiceProperties.CSW_PROFILE_FACTORY_PID) >> managedServiceConfigs
        2 * configReader.getManagedServiceConfigs(_ as String) >> [:]
        report.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, GetCswConfigsAction.ID, managedServiceConfigs.get(S_PID_1), SOURCE_ID_1, S_PID_1, true)
        assertConfig(list.getList().get(1), 1, GetCswConfigsAction.ID, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2, false)
    }

    def 'test service pid filter returns 1 result'() {
        when:
        getCswConfigsAction.setArguments(actionArgs)
        def report = getCswConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getServices(_, _) >> []
        1 * configReader.getConfig(S_PID_2) >>  managedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, GetCswConfigsAction.ID, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2, false)
    }

    def 'test failure due to provided but empty servicePid field'() {
        when:
        getCswConfigsAction.setArguments([(SERVICE_PID) : ''])
        def report = getCswConfigsAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def assertConfig(Field field, int index, String actionId, Map<String, Object> properties, String sourceName, String servicePid, boolean availability) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.fieldName() == ListFieldImpl.INDEX_DELIMETER + index
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.sourceHandlerName() == actionId
        assert sourceInfo.config().endpointUrl() == properties.get(CswServiceProperties.CSW_URL)
        assert sourceInfo.config().credentials().password() == "*****"
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().factoryPid() == F_PID
        assert sourceInfo.config().servicePid() == servicePid
        return true
    }

    def createCswManagedServiceConfigs() {
        managedServiceConfigs = baseManagedServiceConfigs
        managedServiceConfigs.get(S_PID_1).put((EVENT_SERVICE_ADDRESS), TEST_EVENT_SERVICE_ADDRESS)
        managedServiceConfigs.get(S_PID_1).put((CswServiceProperties.CSW_URL), TEST_URL)
        managedServiceConfigs.get(S_PID_2).put((EVENT_SERVICE_ADDRESS), TEST_EVENT_SERVICE_ADDRESS)
        managedServiceConfigs.get(S_PID_2).put((CswServiceProperties.CSW_URL), TEST_URL)
        return managedServiceConfigs
    }


}
