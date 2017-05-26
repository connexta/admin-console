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

import org.codice.ddf.admin.api.Field
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import org.codice.ddf.admin.sources.services.CswServiceProperties
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class GetCswConfigsTest extends Specification {

    static EVENT_SERVICE_ADDRESS = 'eventServiceAddress'

    static TEST_URL = "testCswUrl"

    static TEST_EVENT_SERVICE_ADDRESS = 'testEventServiceAddress'

    static TEST_FACTORY_PID = CswServiceProperties.CSW_PROFILE_FACTORY_PID

    static BASE_PATH = [GetCswConfigurations.ID, FunctionField.ARGUMENT]

    static PID_PATH = [BASE_PATH, PID].flatten()

    GetCswConfigurations getCswConfigsFunction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    def functionArgs = [
        (PID): S_PID_2
    ]

    Map<String, Map<String, Object>> managedServiceConfigs = createCswManagedServiceConfigs()

    def setup() {
        configReader = Mock(ConfigReader)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
        }

        getCswConfigsFunction = new GetCswConfigurations(configuratorFactory)
    }

    def 'No pid argument returns all configs'() {
        when:
        def report = getCswConfigsFunction.getValue()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getManagedServiceConfigs(TEST_FACTORY_PID) >> managedServiceConfigs
        2 * configReader.getManagedServiceConfigs(_ as String) >> [:]
        report.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, managedServiceConfigs.get(S_PID_1), SOURCE_ID_1, S_PID_1, true)
        assertConfig(list.getList().get(1), 1, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2, false)
    }
    def 'Sending pid filter returns 1 result'() {
        when:
        getCswConfigsFunction.setValue(functionArgs)
        def report = getCswConfigsFunction.getValue()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getServices(_, _) >> []
        configReader.getConfig(S_PID_2) >> managedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, managedServiceConfigs.get(S_PID_2), SOURCE_ID_2, S_PID_2, false)
    }

    def 'Fail when there is no existing configuration for the service specified by the pid'() {
        setup:
        functionArgs.put(PID, S_PID)
        getCswConfigsFunction.setValue(functionArgs)
        configReader.getConfig(S_PID) >> [:]

        when:
        def report = getCswConfigsFunction.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == [GetCswConfigurations.ID]
    }

    def assertConfig(Field field, int index, Map<String, Object> properties, String sourceName, String pid, boolean availability) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.fieldName() == ListFieldImpl.INDEX_DELIMETER + index
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.config().endpointUrl() == properties.get(CswServiceProperties.CSW_URL)
        assert sourceInfo.config().credentials().password() == FLAG_PASSWORD
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().pid() == pid
        assert ((CswSourceConfigurationField)sourceInfo.config()).cswProfile() == CswProfile.CSW_FEDERATION_PROFILE_SOURCE
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
}
