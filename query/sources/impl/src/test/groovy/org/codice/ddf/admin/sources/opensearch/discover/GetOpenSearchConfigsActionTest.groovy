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

import ddf.catalog.source.Source
import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.fields.SourceInfoField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class GetOpenSearchConfigsActionTest extends Specification {

    static BASE_PATH = [GetOpenSearchConfigsAction.ID, BaseAction.ARGUMENT]

    static SERVICE_PID_PATH = [BASE_PATH, SERVICE_PID].flatten()

    Action getOpenSearchConfigsAction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    def actionArgs = [
        (SERVICE_PID) : S_PID_2
    ]

    def setup() {
        configReader = Mock(ConfigReader)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
        }
        getOpenSearchConfigsAction = new GetOpenSearchConfigsAction(configuratorFactory)
    }

    def 'test no servicePid argument returns all configs'() {
        when:
        def report = getOpenSearchConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_1, true)]
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getManagedServiceConfigs(_ as String) >> baseManagedServiceConfigs
        report.result() != null
        list.getList().size() == 2
        assertConfig(list.getList().get(0), 0, GetOpenSearchConfigsAction.ID, SOURCE_ID_1, S_PID_1, true)
        assertConfig(list.getList().get(1), 1, GetOpenSearchConfigsAction.ID, SOURCE_ID_2, S_PID_2, false)
    }

    def 'test service pid filter returns 1 result'() {
        setup:
        getOpenSearchConfigsAction.setArguments(actionArgs)

        when:
        def report = getOpenSearchConfigsAction.process()
        def list = ((ListField)report.result())

        then:
        1 * configReader.getServices(_, _) >> [new TestSource(S_PID_2, false)]
        1 * configReader.getServices(_, _) >> []
        1 * configReader.getConfig(S_PID_2) >> baseManagedServiceConfigs.get(S_PID_2)
        report.result() != null
        list.getList().size() == 1
        assertConfig(list.getList().get(0), 0, GetOpenSearchConfigsAction.ID, SOURCE_ID_2, S_PID_2, false)
    }

    def 'test failure due to provided but empty service pid field'() {
        when:
        getOpenSearchConfigsAction.setArguments([(SERVICE_PID) : ''])
        def report = getOpenSearchConfigsAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def assertConfig(Field field, int index, String actionId, String sourceName, String servicePid, boolean availability) {
        def sourceInfo = (SourceInfoField) field
        assert sourceInfo.fieldName() == ListFieldImpl.INDEX_DELIMETER + index
        assert sourceInfo.isAvailable() == availability
        assert sourceInfo.sourceHandlerName() == actionId
        assert sourceInfo.config().credentials().password() == "*****"
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == sourceName
        assert sourceInfo.config().factoryPid() == F_PID
        assert sourceInfo.config().servicePid() == servicePid
        return true
    }

    def mockSource(String sourceName, String pid, boolean availability) {
        def source = Mock(Source)
        source.getId() >> sourceName
        source.isAvailable() >> availability
        return [source]
    }
}
