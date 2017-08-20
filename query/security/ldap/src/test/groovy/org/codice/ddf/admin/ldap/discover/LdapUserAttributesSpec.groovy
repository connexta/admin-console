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
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.api.report.FunctionReport
import org.codice.ddf.admin.common.fields.base.scalar.StringField
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
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.*
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AttributeStore.ATTRIBUTE_STORE

class LdapUserAttributesSpec extends Specification {
    static TestLdapServer server
    LdapUserAttributes action
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
        action = new LdapUserAttributes()

        // Initialize bad paths
        baseMsg = [LdapUserAttributes.FIELD_NAME, FunctionField.ARGUMENT]
        badPaths = [missingHostPath                     : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME],
                    missingPortPath                     : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME],
                    missingEncryptPath                  : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME],
                    missingUsernamePath                 : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                    missingUserpasswordPath             : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                    missingBindMethodPath               : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                    baseUserDnPath                      : baseMsg + [LdapUserAttributes.BASE_USER_DN],
        ]
    }

    def cleanup() {
        ldapConnectionIsClosed = false
    }

    def 'fail on missing required fields'() {
        setup:
        action = new LdapUserAttributes()

        when:
        FunctionReport report = action.execute()

        then:
        report.getErrorMessages().size() == badPaths.size()
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == badPaths.size()

        report.getErrorMessages()*.getPath() as Set == badPaths.values() as Set
    }

    def 'fail to connect to LDAP'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapUserAttributes.BASE_USER_DN)  : LdapTestingCommons.LDAP_SERVER_BASE_GROUP_DN]
        action.setArguments(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'fail to bind to LDAP'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password('badPassword').getValue(),
                (LdapUserAttributes.BASE_USER_DN)  : LdapTestingCommons.LDAP_SERVER_BASE_GROUP_DN]
        action.setArguments(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'succeed'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapUserAttributes.BASE_USER_DN)  : LdapTestingCommons.LDAP_SERVER_BASE_GROUP_DN]
        action.setArguments(args)
        action.setTestingUtils(utilsMock)

        when:
        ListField<StringField> report = action.execute().getResult()

        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        !report.list.empty
        report.list.any {
            it.value == 'cn'
        }
        ldapConnectionIsClosed
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        LdapUserAttributes cannotConnectAction = new LdapUserAttributes()
        Map<String, Object> cannotConnectArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapUserAttributes.BASE_USER_DN)  : LdapTestingCommons.LDAP_SERVER_BASE_GROUP_DN]
        cannotConnectAction.setArguments(cannotConnectArgs)
        cannotConnectAction.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        LdapUserAttributes cannotBindAction = new LdapUserAttributes()
        Map<String, Object> cannotBindArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password('badPassword').getValue(),
                (LdapUserAttributes.BASE_USER_DN)  : LdapTestingCommons.LDAP_SERVER_BASE_GROUP_DN]
        cannotBindAction.setArguments(cannotBindArgs)
        cannotBindAction.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        def errorCodes = action.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectAction.execute()
        def cannotBindReport = cannotBindAction.execute()

        then:
        errorCodes.size() == 3
        errorCodes.contains(cannotConnectReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(cannotBindReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(DefaultMessages.FAILED_TEST_SETUP)
    }

}
