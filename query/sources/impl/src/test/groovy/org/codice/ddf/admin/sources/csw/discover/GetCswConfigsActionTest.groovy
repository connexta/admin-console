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
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.ServicePid
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.services.CswServiceProperties

import spock.lang.Specification


class GetCswConfigsActionTest extends Specification {

    static F_PID = "testFactoryPid"

    static S_PID_1 = "testServicePid1"

    static S_PID_2 = "testServicePid2"

    static SOURCE_ID_1 = "testId1"

    static SOURCE_ID_2 = "testId2"

    static TEST_URL = "testCswUrl"

    static TEST_USERNAME = "admin"

    Action getCswConfigsAction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    def actionArgs = [
        (ServicePid.DEFAULT_FIELD_NAME) : (S_PID_2)
    ]

    Map<String, Map<String, Object>> managedServiceConfigs = [
        (S_PID_1) : [
            password                                 : "admin",
            (CswServiceProperties.CSW_URL)           : TEST_URL,
            eventServiceAddress                      : "testEventServiceAddress",
            id                                       : SOURCE_ID_1,
            (ServiceCommons.FACTORY_PID_KEY): F_PID,
            (ServiceCommons.SERVICE_PID_KEY): S_PID_1,
            username                                 : TEST_USERNAME
        ],
        (S_PID_2) : [
            password : "admin",
            (CswServiceProperties.CSW_URL) : TEST_URL,
            eventServiceAddress : "testEventServiceAddress",
            id : SOURCE_ID_2,
            (ServiceCommons.FACTORY_PID_KEY) : F_PID,
            (ServiceCommons.SERVICE_PID_KEY) : S_PID_2,
            username : TEST_USERNAME
        ]
    ]

    def setup() {
        configReader = Mock(ConfigReader)
        configReader.getManagedServiceConfigs(_ as String) >> [:]

        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
        }

        getCswConfigsAction = new GetCswConfigsAction(configuratorFactory)
    }

    def 'test no servicePid argument returns all configs'() {
        when:
        def result = getCswConfigsAction.process()
        def list = ((ListField)result.result())

        then:
        1 * configReader.getManagedServiceConfigs(CswServiceProperties.CSW_PROFILE_FACTORY_PID) >> managedServiceConfigs
        2 * configReader.getManagedServiceConfigs(_ as String) >> [:]
        result.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, GetCswConfigsAction.ID, managedServiceConfigs.get(S_PID_1), SOURCE_ID_1, S_PID_1)
        assertConfig(list.getList().get(1), 1, GetCswConfigsAction.ID, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2)
    }

    def 'test service pid filter returns 1 result'() {
        when:
        getCswConfigsAction.setArguments(actionArgs)
        def result = getCswConfigsAction.process()
        def list = ((ListField)result.result())

        then:
        1 * configReader.getConfig(S_PID_2) >>  managedServiceConfigs.get(S_PID_2)
        result.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, GetCswConfigsAction.ID, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2)
    }

    def assertConfig(Field field, int index, String actionId, Map<String, Object> properties, String sourceName, String servicePid) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.fieldName() == ListFieldImpl.INDEX_DELIMETER + index
        assert sourceInfo.isAvailable()
        assert sourceInfo.sourceHandlerName() == actionId
        assert sourceInfo.config().endpointUrl() == properties.get(CswServiceProperties.CSW_URL)
        assert sourceInfo.config().credentials().password() == "*****"
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().factoryPid() == F_PID
        assert sourceInfo.config().servicePid() == servicePid
        return true
    }
}
