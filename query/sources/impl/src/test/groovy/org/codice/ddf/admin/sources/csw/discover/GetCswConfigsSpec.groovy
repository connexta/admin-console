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

import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.Field
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.csw.CswSourceInfoField
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.services.CswServiceProperties
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader

class GetCswConfigsSpec extends SourceCommonsSpec {

    static EVENT_SERVICE_ADDRESS = 'eventServiceAddress'

    static TEST_URL = "testCswUrl"

    static TEST_EVENT_SERVICE_ADDRESS = 'testEventServiceAddress'

    static TEST_FACTORY_PID = CswServiceProperties.CSW_PROFILE_FACTORY_PID

    static BASE_PATH = [GetCswConfigurations.FIELD_NAME, FunctionField.ARGUMENT]

    GetCswConfigurations getCswConfigsFunction

    ConfiguratorFactory configuratorFactory

    ServiceReader serviceReader

    ServiceActions serviceActions

    ManagedServiceActions managedServiceActions

    ConfiguratorSuite configuratorSuite

    def functionArgs = [
            (PID): S_PID_2
    ]

    Map<String, Map<String, Object>> managedServiceConfigs = createCswManagedServiceConfigs()

    def setup() {
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        managedServiceActions = Mock(ManagedServiceActions)
        serviceReader = Mock(ServiceReader)

        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.serviceReader >> serviceReader
        configuratorSuite.managedServiceActions >> managedServiceActions

        getCswConfigsFunction = new GetCswConfigurations(configuratorSuite)
    }

    def 'No pid argument returns all configs'() {
        when:
        def report = getCswConfigsFunction.getValue()
        def list = ((ListField) report.getResult())

        then:
        1 * managedServiceActions.read(TEST_FACTORY_PID) >> managedServiceConfigs
        2 * managedServiceActions.read(_ as String) >> [:]
        2 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        2 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        report.getResult() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, managedServiceConfigs.get(S_PID_1), SOURCE_ID_1, S_PID_1, true)
        assertConfig(list.getList().get(1), 1, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2, false)
    }

    def 'Sending pid filter returns 1 result'() {
        when:
        getCswConfigsFunction.setValue(functionArgs)
        def report = getCswConfigsFunction.getValue()
        def list = ((ListField) report.getResult())

        then:
        1 * serviceReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * serviceReader.getServices(_, _) >> []
        serviceActions.read(S_PID_2) >> managedServiceConfigs.get(S_PID_2)
        report.getResult() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2, false)
    }

    def 'Fail when there is no existing configuration for the service specified by the pid'() {
        setup:
        functionArgs.put(PID, S_PID)
        getCswConfigsFunction.setValue(functionArgs)
        serviceActions.read(S_PID) >> [:]

        when:
        def report = getCswConfigsFunction.getValue()

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.getErrorMessages().get(0).path == [GetCswConfigurations.FIELD_NAME]
    }

    def assertConfig(Field field, int index, Map<String, Object> properties, String sourceName, String pid, boolean availability) {
        def sourceInfo = (CswSourceInfoField) field
        assert sourceInfo.path()[-1] == index.toString()
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.config().endpointUrl() == properties.get(CswServiceProperties.CSW_URL)
        assert sourceInfo.config().credentials().password() == FLAG_PASSWORD
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().pid() == pid
        assert sourceInfo.config().cswProfile() == CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE
        return true
    }

    def createCswManagedServiceConfigs() {
        managedServiceConfigs = baseManagedServiceConfigs
        managedServiceConfigs.get(S_PID_1).put((EVENT_SERVICE_ADDRESS), TEST_EVENT_SERVICE_ADDRESS)
        managedServiceConfigs.get(S_PID_1).put((CswServiceProperties.CSW_URL), TEST_URL)
        managedServiceConfigs.get(S_PID_1).put(FACTORY_PID_KEY, TEST_FACTORY_PID)
        managedServiceConfigs.get(S_PID_2).put((EVENT_SERVICE_ADDRESS), TEST_EVENT_SERVICE_ADDRESS)
        managedServiceConfigs.get(S_PID_2).put((CswServiceProperties.CSW_URL), TEST_URL)
        managedServiceConfigs.get(S_PID_2).put(FACTORY_PID_KEY, TEST_FACTORY_PID)
        return managedServiceConfigs
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        GetCswConfigurations noExistingConfigFunc = new GetCswConfigurations(configuratorSuite)
        functionArgs.put(PID, S_PID)
        noExistingConfigFunc.setValue(functionArgs)
        serviceActions.read(S_PID) >> [:]

        when:
        def errorCodes = getCswConfigsFunction.getFunctionErrorCodes()
        def noExistingConfigReport = noExistingConfigFunc.getValue()

        then:
        errorCodes.size() == 1
        errorCodes.contains(noExistingConfigReport.getErrorMessages().get(0).getCode())
    }
}
