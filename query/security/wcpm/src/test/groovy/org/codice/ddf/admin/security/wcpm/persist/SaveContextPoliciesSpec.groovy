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

import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import org.codice.ddf.admin.api.FieldProvider
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.poller.EnumValuePoller
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.common.fields.base.BaseFunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.security.common.SecurityMessages
import org.codice.ddf.admin.security.common.fields.wcpm.AuthType
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin
import org.codice.ddf.admin.security.common.fields.wcpm.Realm
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties
import org.codice.ddf.admin.security.common.services.StsServiceProperties
import org.codice.ddf.admin.security.wcpm.AuthTypesPoller
import org.codice.ddf.admin.security.wcpm.RealmTypesPoller
import org.codice.ddf.admin.security.wcpm.WcpmFieldProvider
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import org.codice.ddf.security.policy.context.impl.PolicyManager
import spock.lang.Specification

import static groovy.org.codice.ddf.admin.security.wcpm.persist.WcpmTestingCommons.*

class SaveContextPoliciesSpec extends Specification {

    static final List<Object> FUNCTION_PATH = [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME]
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
    AuthTypesPoller authTypesPoller
    RealmTypesPoller realmTypesPoller

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
                                authTypes    : [BASIC],
                                realm        :  KARAF,
                                claimsMapping: [
                                        [
                                                (ClaimsMapEntry.KEY_FIELD_NAME)  : 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role',
                                                (ClaimsMapEntry.VALUE_FIELD_NAME): 'system-admin'
                                        ]
                                ]
                        ],
                        [
                                paths        : ['/test'],
                                authTypes    : [SAML, PKI],
                                realm        : KARAF,
                                claimsMapping: [
                                        [
                                                (ClaimsMapEntry.KEY_FIELD_NAME)  : 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role',
                                                (ClaimsMapEntry.VALUE_FIELD_NAME): 'system-admin'
                                        ]
                                ]
                        ]
                ]
        ]

        authTypesPoller = new AuthTypesPoller()
        authTypesPoller.setAuthHandlers([BASIC_HANDLER, SAML_HANDLER, PKI_HANDLER])
        realmTypesPoller = new RealmTypesPoller()
        realmTypesPoller.setRealms([KARAF_REALM])

        operationReport = Mock(OperationReport)
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        serviceReader = Mock(ServiceReader)
        configurator = Mock(Configurator)

        configurator.commit(_, _) >> operationReport
        serviceReader.getServiceReference(_) >> policyManager
        serviceReader.getServices(EnumValuePoller.class, AuthType.AUTH_TYPE_POLLER_FILTER) >> ([authTypesPoller] as Set)
        serviceReader.getServices(EnumValuePoller.class, Realm.REALM_POLLER_FILTER) >> ([realmTypesPoller] as Set)
        configuratorFactory.getConfigurator() >> configurator
        stsConfig = [(StsServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS): testClaims]
        serviceActions.read(_) >> stsConfig

        policyManager = new PolicyManager()
        ContextPolicyBin.ListImpl contextPolicies = new ContextPolicyBin.ListImpl(serviceReader)
        contextPolicies.setValue(testData.policies)
        policyManager.setPolicies(new PolicyManagerServiceProperties().contextPoliciesToPolicyManagerProps(contextPolicies.getList()))

        def configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.serviceReader >> serviceReader
        queryProvider = new WcpmFieldProvider(configuratorSuite)
        saveContextPoliciesFunction = queryProvider.getMutationFunction(SaveContextPolices.FUNCTION_FIELD_NAME)
    }

    def 'Pass with valid update'() {
        setup:
        operationReport.containsFailedResults() >> false

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().isEmpty()
        report.getResult().getValue() == testData.policies
    }

    def 'Fail when failed to persist'() {
        setup:
        operationReport.containsFailedResults() >> true

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME]
        report.getResult() == null
    }

    def 'Fail if no root context is present'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].paths = ['/test']

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == SecurityMessages.NO_ROOT_CONTEXT
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies']
        report.getResult() == null
    }

    def 'Fail if invalid authType'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].authTypes = [PKI, 'COFFEE']

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == DefaultMessages.UNSUPPORTED_ENUM
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 0, 'authTypes', 1]
        report.getResult() == null
    }

    def 'Fail if no authType'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].authTypes = []

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 0, 'authTypes']
        report.getResult() == null

    }

    def 'Fail if invalid realm'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].realm = 'COFFEE'

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == DefaultMessages.UNSUPPORTED_ENUM
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 0, Realm.DEFAULT_FIELD_NAME]
        report.getResult() == null
    }

    def 'Fail if no realm'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].realm = null

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 0, Realm.DEFAULT_FIELD_NAME]
        report.getResult() == null
    }

    def 'Pass if no claims Mapping'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].claimsMapping = []

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().isEmpty()
        report.getResult().getValue() == testData.policies
    }

    def 'Fail if claim entry with no value '() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].claimsMapping[0][ClaimsMapEntry.VALUE_FIELD_NAME] = null

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 0, 'claimsMapping', 0, ClaimsMapEntry.VALUE_FIELD_NAME]
        report.getResult() == null
    }

    def 'Fail if claim is not supported'() {
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[0].claimsMapping[0][ClaimsMapEntry.KEY_FIELD_NAME] = 'unsupportedClaim'

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].code == SecurityMessages.INVALID_CLAIM_TYPE
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 0, 'claimsMapping', 0, ClaimsMapEntry.KEY_FIELD_NAME]
        report.getResult() == null
    }

    def 'Fail when context path has a trailing slash'(){
        setup:
        operationReport.containsFailedResults() >> false
        testData.policies[1].paths = ['/test/', '/test', 'wrong/', '/invalid/']

        when:
        Report report = saveContextPoliciesFunction.execute(testData, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 4

        report.getErrorMessages()[0].code == DefaultMessages.INVALID_CONTEXT_PATH
        report.getErrorMessages()[0].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 1, 'paths', 2]

        report.getErrorMessages()[1].code == DefaultMessages.INVALID_PATH_TRAILING_SLASH
        report.getErrorMessages()[1].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 1, 'paths', 0]

        report.getErrorMessages()[2].code == DefaultMessages.INVALID_PATH_TRAILING_SLASH
        report.getErrorMessages()[2].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 1, 'paths', 2]

        report.getErrorMessages()[3].code == DefaultMessages.INVALID_PATH_TRAILING_SLASH
        report.getErrorMessages()[3].path == [WcpmFieldProvider.NAME, SaveContextPolices.FUNCTION_FIELD_NAME, 'policies', 1, 'paths', 3]
        report.getResult() == null
    }

    def 'Returns all the possible error codes correctly'(){
        when:
        def errorCodes = saveContextPoliciesFunction.getErrorCodes()

        then:
        errorCodes.size() == 9
        errorCodes.contains(DefaultMessages.FAILED_PERSIST)
        errorCodes.contains(DefaultMessages.INVALID_CONTEXT_PATH)
        errorCodes.contains(DefaultMessages.MISSING_REQUIRED_FIELD)
        errorCodes.contains(DefaultMessages.EMPTY_FIELD)
        errorCodes.contains(DefaultMessages.MISSING_KEY_VALUE)
        errorCodes.contains(DefaultMessages.UNSUPPORTED_ENUM)
        errorCodes.contains(DefaultMessages.INVALID_PATH_TRAILING_SLASH)
        errorCodes.contains(SecurityMessages.INVALID_CLAIM_TYPE)
        errorCodes.contains(SecurityMessages.NO_ROOT_CONTEXT)
    }
}