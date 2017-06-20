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
import org.codice.ddf.admin.security.common.SecurityMessages
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin
import org.codice.ddf.admin.security.common.fields.wcpm.Realm
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties
import org.codice.ddf.admin.security.common.services.StsServiceProperties
import org.codice.ddf.admin.security.wcpm.WcpmFieldProvider
import org.codice.ddf.internal.admin.configurator.actions.BundleActions
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import org.codice.ddf.security.policy.context.impl.PolicyManager
import spock.lang.Specification

class SaveContextPoliciesTest extends Specification {
    FieldProvider queryProvider
    ConfiguratorFactory configuratorFactory
    ServiceActions serviceActions
    ServiceReader serviceReader
    Configurator configurator
    OperationReport operationReport
    PolicyManager policyManager
    FunctionField saveContextPoliciesFunction
    Map<String, Object> stsConfig

    String[] testClaims
    def testData

    def setup() {
        testClaims = [
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier",
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress",
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname",
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname",
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role"
        ]

        testData = [
                policies: [
                        [
                                paths        : ['/'],
                                authTypes    : ['basic'],
                                realm        : 'karaf',
                                claimsMapping: [
                                        [
                                                (ClaimsMapEntry.KEY_FIELD_NAME)  : 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role',
                                                (ClaimsMapEntry.VALUE_FIELD_NAME): 'system-admin'
                                        ]
                                ]
                        ],
                        [
                                paths        : ['/test'],
                                authTypes    : ['SAML', 'PKI'],
                                realm        : 'karaf',
                                claimsMapping: [
                                        [
                                                (ClaimsMapEntry.KEY_FIELD_NAME)  : 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role',
                                                (ClaimsMapEntry.VALUE_FIELD_NAME): 'system-admin'
                                        ]
                                ]
                        ]
                ]
        ]


        operationReport = Mock(OperationReport)
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        serviceReader = Mock(ServiceReader)
        configurator = Mock(Configurator)

        stsConfig = [(StsServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS): testClaims]
        serviceActions.read(_) >> stsConfig

        policyManager = new PolicyManager()
        ContextPolicyBin.ContextPolicies contextPolicies = new ContextPolicyBin.ContextPolicies()
        contextPolicies.setValue(testData.policies)
        policyManager.setPolicies(new PolicyManagerServiceProperties().contextPoliciesToPolicyManagerProps(contextPolicies.getList()))

        configurator.commit(_, _) >> operationReport
        serviceReader.getServiceReference(_) >> policyManager
        configuratorFactory.getConfigurator() >> configurator

        def bundleActions = Mock(BundleActions)
        def managedServiceActions = Mock(ManagedServiceActions)

        queryProvider = new WcpmFieldProvider(configuratorFactory, serviceActions, bundleActions,
                managedServiceActions, serviceReader)
        saveContextPoliciesFunction = queryProvider.getMutationFunction(SaveContextPolices.FUNCTION_FIELD_NAME)
    }

    def 'Pass with valid update'() {
        setup:
        operationReport.containsFailedResults() >> false

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().isEmpty()
        report.result().getValue() == testData.policies
    }

    def 'Fail when failed to persist'() {
        setup:
        operationReport.containsFailedResults() >> true

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == DefaultMessages.FAILED_PERSIST
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME]
        report.result() == null
    }

    def 'Fail if no root context is present'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].paths = ['/test']

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == SecurityMessages.NO_ROOT_CONTEXT
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, BaseFunctionField.ARGUMENT, 'policies']
        report.result() == null
    }

    def 'Fail if invalid authType'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].authTypes = ['PKI', 'COFFEE']

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == DefaultMessages.UNSUPPORTED_ENUM
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, BaseFunctionField.ARGUMENT, 'policies', '0', 'authTypes', '1']
        report.result() == null
    }

    def 'Fail if no authType'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].authTypes = []

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, BaseFunctionField.ARGUMENT, 'policies', '0', 'authTypes']
        report.result() == null

    }

    def 'Fail if invalid realm'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].realm = 'COFFEE'

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == DefaultMessages.UNSUPPORTED_ENUM
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, BaseFunctionField.ARGUMENT, 'policies', '0', Realm.DEFAULT_FIELD_NAME]
        report.result() == null
    }

    def 'Fail if no realm'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].realm = null

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, BaseFunctionField.ARGUMENT, 'policies', '0', Realm.DEFAULT_FIELD_NAME]
        report.result() == null
    }

    def 'Pass if no claims Mapping'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].claimsMapping = []

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().isEmpty()
        report.result().getValue() == testData.policies
    }

    def 'Fail if claim entry with no value '() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].claimsMapping[0][ClaimsMapEntry.VALUE_FIELD_NAME] = null

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, BaseFunctionField.ARGUMENT, 'policies', '0', 'claimsMapping', '0', ClaimsMapEntry.VALUE_FIELD_NAME]
        report.result() == null
    }

    def 'Fail if claim is not supported'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].claimsMapping[0][ClaimsMapEntry.KEY_FIELD_NAME] = 'unsupportedClaim'

        when:
        saveContextPoliciesFunction.setValue(testData)
        ReportWithResult report = saveContextPoliciesFunction.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].code == SecurityMessages.INVALID_CLAIM_TYPE
        report.messages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, BaseFunctionField.ARGUMENT, 'policies', '0', 'claimsMapping', '0', ClaimsMapEntry.KEY_FIELD_NAME]
        report.result() == null
    }
}