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
package org.codice.ddf.admin.security.wcpm.persist

import org.codice.ddf.admin.api.FieldProvider
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.api.report.ReportWithResult
import org.codice.ddf.admin.common.fields.base.BaseFunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties
import org.codice.ddf.admin.security.wcpm.WcpmFieldProvider
import org.codice.ddf.internal.admin.configurator.actions.BundleActions
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import org.codice.ddf.security.policy.context.impl.PolicyManager
import spock.lang.Specification

class SaveWhitelistContextsTest extends Specification {
    FieldProvider WcpmFieldProvider
    ConfiguratorFactory configuratorFactory
    ServiceActions serviceActions
    Configurator configurator
    OperationReport operationReport
    PolicyManager policyManager
    FunctionField saveWhitelistContextsFunction

    def setup() {
        operationReport = Mock(OperationReport)
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        configurator = Mock(Configurator)
        policyManager = new PolicyManager()

        policyManager.setWhiteListContexts(['/', '/default', '/paths'])
        configurator.commit(_, _) >> operationReport
        configuratorFactory.getConfigurator() >> configurator
        serviceActions.read(_) >> {
            [(PolicyManagerServiceProperties.WHITE_LIST_CONTEXT): policyManager.getWhiteListContexts()]
        }


        WcpmFieldProvider = new WcpmFieldProvider(configuratorFactory,
                serviceActions,
                Mock(BundleActions),
                Mock(ManagedServiceActions),
                Mock(ServiceReader))
        saveWhitelistContextsFunction = WcpmFieldProvider.getMutationFunction(SaveWhitelistContexts.FIELD_NAME)
    }

    def 'Pass with valid context list'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '/']]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        ReportWithResult report = saveWhitelistContextsFunction.getValue()

        then:
        report.messages().size() == 0
        report.result().getValue() == testMap.paths
    }

    def 'Fail if context path is invalid'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '!@#(%^$(&(*']]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        ReportWithResult report = saveWhitelistContextsFunction.getValue()

        then:
        report.messages()[0].code == DefaultMessages.INVALID_CONTEXT_PATH
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveWhitelistContexts.FIELD_NAME, BaseFunctionField.ARGUMENT, 'paths', ListField.INDEX_DELIMETER + 2]
        report.result() == null
    }

    def 'Fail if context path is empty'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '']]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        ReportWithResult report = saveWhitelistContextsFunction.getValue()

        then:
        report.messages()[0].code == DefaultMessages.EMPTY_FIELD
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveWhitelistContexts.FIELD_NAME, BaseFunctionField.ARGUMENT, 'paths', ListField.INDEX_DELIMETER + 2]
        report.result() == null
    }

    def 'Pass if list is empty (whitelist contexts not required)'() {
        setup:
        def testMap = ['paths': []]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        ReportWithResult report = saveWhitelistContextsFunction.getValue()

        then:
        report.messages().size() == 0
        report.result().getValue() == []
    }

    def 'Fail when fail to persist'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '/']]
        operationReport.containsFailedResults() >> true

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        ReportWithResult report = saveWhitelistContextsFunction.getValue()

        then:
        report.messages()[0].code == DefaultMessages.FAILED_PERSIST
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveWhitelistContexts.FIELD_NAME]
        report.result() == null
    }
}