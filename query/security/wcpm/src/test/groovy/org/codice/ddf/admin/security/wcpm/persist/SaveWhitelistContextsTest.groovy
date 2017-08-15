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

import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.common.fields.base.BaseFunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties
import org.codice.ddf.admin.security.wcpm.WcpmFieldProvider
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import org.codice.ddf.security.policy.context.impl.PolicyManager
import spock.lang.Specification

class SaveWhitelistContextsTest extends Specification {
    WcpmFieldProvider wcpmFieldProvider
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

        def configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.serviceReader >> Mock(ServiceReader)

        wcpmFieldProvider = new WcpmFieldProvider(configuratorSuite)

        saveWhitelistContextsFunction = wcpmFieldProvider.getMutationFunction(SaveWhitelistContexts.FIELD_NAME)
    }

    def 'Pass with valid context list'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '/']]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        Report report = saveWhitelistContextsFunction.getValue()

        then:
        report.getErrorMessages().size() == 0
        report.getResult().getValue() == testMap.paths
    }

    def 'Fail if context path is invalid'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '!@#(%^$(&(*']]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        Report report = saveWhitelistContextsFunction.getValue()

        then:
        report.getErrorMessages()[0].code == DefaultMessages.INVALID_CONTEXT_PATH
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveWhitelistContexts.FIELD_NAME, BaseFunctionField.ARGUMENT, 'paths', '2']
        report.getResult() == null
    }

    def 'Fail if context path is empty'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '']]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        Report report = saveWhitelistContextsFunction.getValue()

        then:
        report.getErrorMessages()[0].code == DefaultMessages.EMPTY_FIELD
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveWhitelistContexts.FIELD_NAME, BaseFunctionField.ARGUMENT, 'paths', '2']
        report.getResult() == null
    }

    def 'Pass if list is empty (whitelist contexts not required)'() {
        setup:
        def testMap = ['paths': []]
        operationReport.containsFailedResults() >> false

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        Report report = saveWhitelistContextsFunction.getValue()

        then:
        report.getErrorMessages().size() == 0
        report.getResult().getValue() == []
    }

    def 'Fail when fail to persist'() {
        setup:
        def testMap = ['paths': ['/test', '/path', '/']]
        operationReport.containsFailedResults() >> true

        when:
        saveWhitelistContextsFunction.setValue(testMap)
        Report report = saveWhitelistContextsFunction.getValue()

        then:
        report.getErrorMessages()[0].code == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages()[0].path == [wcpmFieldProvider.NAME, SaveWhitelistContexts.FIELD_NAME]
        report.getResult() == null
    }

    def 'Returns all the possible error codes correctly'(){
        when:
        def errorCodes = saveWhitelistContextsFunction.getErrorCodes()

        then:
        errorCodes.size() == 4
        errorCodes.contains(DefaultMessages.FAILED_PERSIST)
        errorCodes.contains(DefaultMessages.INVALID_CONTEXT_PATH)
        errorCodes.contains(DefaultMessages.MISSING_REQUIRED_FIELD)
        errorCodes.contains(DefaultMessages.EMPTY_FIELD)
    }
}