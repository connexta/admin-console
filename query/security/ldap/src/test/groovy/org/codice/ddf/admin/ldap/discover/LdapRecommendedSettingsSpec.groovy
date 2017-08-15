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
package org.codice.ddf.admin.ldap.discover

import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.report.FunctionReport
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.LdapTestingCommons
import org.codice.ddf.admin.ldap.TestLdapServer
import org.codice.ddf.admin.ldap.commons.LdapMessages
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.ldap.fields.query.LdapRecommendedSettingsField
import org.codice.ddf.admin.ldap.fields.query.LdapTypeField
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.noEncryptionLdapConnectionInfo
import static org.codice.ddf.admin.ldap.LdapTestingCommons.simpleBindInfo

class LdapRecommendedSettingsSpec extends Specification {
    static TestLdapServer server
    LdapRecommendedSettings action
    Map<String, Object> args
    def badPaths
    def baseMsg
    LdapTestingUtils utilsMock
    boolean ldapConnectionIsClosed

    def setupSpec() {
        server = TestLdapServer.getInstance().useSimpleAuth()
        server.startListening()
    }

    def cleanupSpec() {
        server.shutdown()
        server = null
    }

    def setup() {
        utilsMock = new LdapTestConnectionSpec.LdapTestingUtilsMock()
        LdapTestingCommons.loadLdapTestProperties()
        action = new LdapRecommendedSettings()

        // Initialize bad paths
        baseMsg = [LdapRecommendedSettings.FIELD_NAME, FunctionField.ARGUMENT]
        badPaths = [missingHostPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME],
                    missingPortPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME],
                    missingEncryptPath     : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME],
                    missingUsernamePath    : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                    missingUserpasswordPath: baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                    missingBindMethodPath  : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                    missingLdapTypePath    : baseMsg + [LdapTypeField.DEFAULT_FIELD_NAME]
        ]
    }

    def cleanup() {
        ldapConnectionIsClosed = false
    }

    def 'Fail on missing required fields'() {
        setup:
        action = new LdapRecommendedSettings()

        when:
        FunctionReport report = action.getValue()

        then:
        report.getErrorMessages().size() == 7
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 7

        report.getErrorMessages()*.getPath() as Set == [badPaths.missingHostPath,
                                                badPaths.missingPortPath,
                                                badPaths.missingEncryptPath,
                                                badPaths.missingUsernamePath,
                                                badPaths.missingUserpasswordPath,
                                                badPaths.missingBindMethodPath,
                                                badPaths.missingLdapTypePath] as Set
    }

    def 'fail to connect to LDAP'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapTypeField.DEFAULT_FIELD_NAME)      : LdapTypeField.Unknown.UNKNOWN]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'fail to bind to LDAP'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password('badPassword').getValue(),
                (LdapTypeField.DEFAULT_FIELD_NAME)      : LdapTypeField.Unknown.UNKNOWN]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'validate settings successfully'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapTypeField.DEFAULT_FIELD_NAME)      : LdapTypeField.Unknown.UNKNOWN]
        action.setValue(args)
        action.setTestingUtils(utilsMock)

        when:
        LdapRecommendedSettingsField recSettings = action.getValue().getResult()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        recSettings.userDnsField().value.size() == 1
        recSettings.userDnsField().value.first() == 'ou=users,dc=example,dc=com'

        recSettings.groupDnsField().value.size() == 2
        recSettings.groupDnsField().value.any {
            it == 'ou=groups,dc=example,dc=com'
        }

        recSettings.userNameAttributesField().value.size() == 1
        recSettings.userNameAttributesField().value.first() == 'uid'

        recSettings.groupObjectClassesField().value.collect {
            it.toLowerCase()
        } as Set == ['group', 'groupofnames', 'posixgroup'] as Set

        recSettings.groupAttributesHoldingMemberField().value.collect {
            it.toLowerCase()
        } as Set == ['member', 'uniquemember', 'memberuid'] as Set

        recSettings.memberAttributesReferencedInGroupField().value == ['uid']

        recSettings.queryBasesField().value == [TestLdapServer.getBaseDistinguishedName()]

        ldapConnectionIsClosed
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        LdapRecommendedSettings cannotBindSettings = new LdapRecommendedSettings()
        Map<String, Object> cannotBindArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password('badPassword').getValue(),
                (LdapTypeField.DEFAULT_FIELD_NAME)      : LdapTypeField.Unknown.UNKNOWN]
        cannotBindSettings.setValue(cannotBindArgs)
        cannotBindSettings.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        LdapRecommendedSettings cannotConnectSettings = new LdapRecommendedSettings()
        Map<String, Object> cannotConnectArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapTypeField.DEFAULT_FIELD_NAME)      : LdapTypeField.Unknown.UNKNOWN]
        cannotConnectSettings.setValue(cannotConnectArgs)
        cannotConnectSettings.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        def errorCodes = action.getFunctionErrorCodes()
        def cannotBindReport = cannotBindSettings.getValue()
        def cannotConnectReport = cannotConnectSettings.getValue()

        then:
        errorCodes.size() == 3
        errorCodes.contains(cannotBindReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(cannotConnectReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(DefaultMessages.FAILED_TEST_SETUP)
    }
}
