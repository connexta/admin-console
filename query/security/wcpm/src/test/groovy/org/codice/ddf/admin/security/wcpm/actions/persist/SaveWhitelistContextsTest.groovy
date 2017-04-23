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
package org.codice.ddf.admin.security.wcpm.actions.persist

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.action.ActionCreator
import org.codice.ddf.admin.api.action.ActionReport
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.security.wcpm.actions.WcpmActionCreator
import org.codice.ddf.security.policy.context.impl.PolicyManager
import spock.lang.Specification

class SaveWhitelistContextsTest extends Specification {
    ActionCreator actionCreator
    ConfiguratorFactory configuratorFactory
    Configurator configurator
    ConfigReader configReader
    OperationReport operationReport
    PolicyManager policyManager
    Action action

    def setup() {
        operationReport = Mock(OperationReport)
        configuratorFactory = Mock(ConfiguratorFactory)
        configurator = Mock(Configurator)
        policyManager = new PolicyManager()
        configReader = Mock(ConfigReader)

        policyManager.setWhiteListContexts([ '/', '/default', '/paths' ])
        configurator.commit(_,_) >> operationReport
        configuratorFactory.getConfigurator() >> configurator
        configuratorFactory.getConfigReader() >>  configReader
        configReader.getServiceReference(_) >> policyManager
        actionCreator = new WcpmActionCreator(configuratorFactory)
        action = actionCreator.createAction(SaveWhitelistContexts.ACTION_ID)
    }

    def 'Pass with valid context list' () {
        setup:
        def testMap = [ 'paths':[  '/test', '/path', '/' ] ]
        operationReport.containsFailedResults() >> false

        when:
        action.setArguments(testMap)
        ActionReport report = action.process()

        then:
        report.messages().size() == 0
        report.result().getValue() == testMap.paths
    }

    def 'Fail if context path is invalid' () {
        setup:
        def testMap = [ 'paths':[  '/test', '/path', '!@#(%^$(&(*' ] ]
        operationReport.containsFailedResults() >> false

        when:
        action.setArguments(testMap)
        ActionReport report = action.process()

        then:
        report.messages()[0].code == 'INVALID_CONTEXT_PATH'
        report.messages()[0].path == [SaveWhitelistContexts.ACTION_ID, BaseAction.ARGUMENT, 'paths', ListFieldImpl.INDEX_DELIMETER + 2]
    }

    def 'Fail if context path is empty' () {
        setup:
        def testMap = [ 'paths':[  '/test', '/path', '' ] ]
        operationReport.containsFailedResults() >> false

        when:
        action.setArguments(testMap)
        ActionReport report = action.process()

        then:
        report.messages()[0].code == 'EMPTY_FIELD'
        report.messages()[0].path == [SaveWhitelistContexts.ACTION_ID, BaseAction.ARGUMENT, 'paths', ListFieldImpl.INDEX_DELIMETER + 2]
    }


    def 'Pass if list is empty (whitelist contexts not required)' () {
        setup:
        def testMap = [ 'paths':[] ]
        operationReport.containsFailedResults() >> false

        when:
        action.setArguments(testMap)
        ActionReport report = action.process()

        then:
        report.messages().size() == 0
        report.result().getValue() == []
    }

    def 'Fail when fail to persist' () {
        setup:
        def testMap = [ 'paths':[  '/test', '/path', '/' ] ]
        operationReport.containsFailedResults() >> true

        when:
        action.setArguments(testMap)
        ActionReport report = action.process()

        then:
        report.messages()[0].code == 'FAILED_PERSIST'
        report.messages()[0].path == [SaveWhitelistContexts.ACTION_ID]
    }
}