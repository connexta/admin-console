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

import org.codice.ddf.admin.api.report.FunctionReport
import org.codice.ddf.admin.common.fields.base.BaseFunctionField
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.LdapTestingCommons
import org.codice.ddf.admin.ldap.TestLdapServer
import org.codice.ddf.admin.ldap.commons.LdapMessages
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.ldap.fields.query.LdapQueryField
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.noEncryptionLdapConnectionInfo
import static org.codice.ddf.admin.ldap.LdapTestingCommons.simpleBindInfo

class LdapQuerySpec extends Specification {
    static TestLdapServer server
    Map<String, Object> args
    LdapQuery ldapQueryFunction
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
        ldapQueryFunction = new LdapQuery()
    }

    def cleanup() {
        ldapConnectionIsClosed = false
    }

    def 'Fail on missing required fields'() {
        setup:
        def baseMsg = [LdapQuery.FIELD_NAME, BaseFunctionField.ARGUMENT]
        def missingHostMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME]
        def missingPortMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME]
        def missingEncryptMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME]
        def missingUsernameMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME]
        def missingUserpasswordMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME]
        def missingBindMethodMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME]
        def missingQueryBaseMsgPath = baseMsg + [LdapQuery.QUERY_BASE_FIELD_NAME]
        def missingQueryMsgPath = baseMsg + [LdapQueryField.DEFAULT_FIELD_NAME]

        when:
        FunctionReport report = ldapQueryFunction.getValue()

        then:
        report.getErrorMessages().size() == 8
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 8

        report.getErrorMessages()*.getPath() as Set == [missingHostMsgPath, missingPortMsgPath, missingEncryptMsgPath,
                                                missingUsernameMsgPath, missingUserpasswordMsgPath, missingBindMethodMsgPath,
                                                missingQueryBaseMsgPath, missingQueryMsgPath] as Set
    }

    def 'Fail to connect to LDAP'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : exampleLdapQuery().getValue()]
        ldapQueryFunction.setValue(args)

        when:
        FunctionReport report = ldapQueryFunction.getValue()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.getErrorMessages().get(0).getPath() == [LdapQuery.FIELD_NAME, BaseFunctionField.ARGUMENT, LdapConnectionField.DEFAULT_FIELD_NAME]

    }

    def 'Fail to bind user over bind method "Simple" with bad password'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("badPassword").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : exampleLdapQuery().getValue()]

        ldapQueryFunction.setValue(args)

        when:
        FunctionReport report = ldapQueryFunction.getValue()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == [LdapQuery.FIELD_NAME, BaseFunctionField.ARGUMENT, LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'Successfully query with entries found (No encryption)'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("secret").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : ldapQuery("(objectClass=person)").getValue()]

        ldapQueryFunction.setValue(args)
        ldapQueryFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapQueryFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        def entries = report.getResult().getList()
        entries.size() == 2
        entries*.getEntry('name')*.get()*.value() as Set ==
                ['uid=tstark,ou=users,dc=example,dc=com', 'uid=bbanner,ou=users,dc=example,dc=com'] as Set
        entries*.getEntry('employeeType')*.get()*.value() as Set == ['ironman', 'hulk'] as Set
        ldapConnectionIsClosed
    }

    def 'Successfully query with entries found (startTLS) '() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): startTlsLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("secret").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : ldapQuery("(objectClass=person)").getValue()]

        ldapQueryFunction.setValue(args)
        ldapQueryFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapQueryFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        def entries = report.getResult().getList()
        entries.size() == 2
        entries*.getEntry('name')*.get()*.value() as Set ==
                ['uid=tstark,ou=users,dc=example,dc=com', 'uid=bbanner,ou=users,dc=example,dc=com'] as Set
        entries*.getEntry('employeeType')*.get()*.value() as Set == ['ironman', 'hulk'] as Set
        ldapConnectionIsClosed
    }

    def 'Successfully query with entries found (LDAPS) '() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("secret").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : ldapQuery("(objectClass=person)").getValue()]

        ldapQueryFunction.setValue(args)
        ldapQueryFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapQueryFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        def entries = report.getResult().getList()
        entries.size() == 2
        entries*.getEntry('name')*.get()*.value() as Set ==
                ['uid=tstark,ou=users,dc=example,dc=com', 'uid=bbanner,ou=users,dc=example,dc=com'] as Set
        entries*.getEntry('employeeType')*.get()*.value() as Set == ['ironman', 'hulk'] as Set
        ldapConnectionIsClosed
    }

    def 'Successfully query with no entries found'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("secret").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : ldapQuery("(uid=NOT_REAL)").getValue()]

        ldapQueryFunction.setValue(args)
        ldapQueryFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapQueryFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        def entries = report.getResult().getList()
        entries.size() == 0
        ldapConnectionIsClosed
    }

    def 'Successfully query with specified max entry results'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("secret").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQuery.MAX_QUERY_FIELD_NAME)        : 1,
                (LdapQueryField.DEFAULT_FIELD_NAME)     : ldapQuery("(objectClass=person)").getValue()]

        ldapQueryFunction.setValue(args)
        ldapQueryFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapQueryFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        def entries = report.getResult().getList()
        entries.size() == 1
        entries.first().getEntry('name').get().value() in
                ['uid=tstark,ou=users,dc=example,dc=com', 'uid=bbanner,ou=users,dc=example,dc=com']
        entries.first().getEntry('employeeType').get().value() in ['ironman', 'hulk']
        ldapConnectionIsClosed
    }

    def 'Successfully filter password fields'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("secret").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : ldapQuery("(objectClass=person)").getValue()]

        ldapQueryFunction.setValue(args)
        ldapQueryFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapQueryFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        def entries = report.getResult().getList()
        entries.size() == 2
        entries*.getEntry('name')*.get()*.value() as Set ==
                ['uid=tstark,ou=users,dc=example,dc=com', 'uid=bbanner,ou=users,dc=example,dc=com'] as Set
        entries*.getEntry('employeeType')*.get()*.value() as Set == ['ironman', 'hulk'] as Set

        then: 'ensure no password data returned'
        entries*.getEntry('password') == [Optional.empty(), Optional.empty()]
        ldapConnectionIsClosed
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        Map<String, Object> cannotConnectArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : exampleLdapQuery().getValue()]
        LdapQuery cannotConnectQuery = new LdapQuery()
        cannotConnectQuery.setValue(cannotConnectArgs)


        Map<String, Object> cannotBindArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("badPassword").getValue(),
                (LdapQuery.QUERY_BASE_FIELD_NAME)       : exampleQueryBase().getValue(),
                (LdapQueryField.DEFAULT_FIELD_NAME)     : exampleLdapQuery().getValue()]
        LdapQuery cannotBindQuery = new LdapQuery()
        cannotBindQuery.setValue(cannotBindArgs)

        when:
        def errorCodes = ldapQueryFunction.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectQuery.getValue()
        def cannotBindReport = cannotBindQuery.getValue()

        then:
        errorCodes.size() == 3
        errorCodes.contains(cannotConnectReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(cannotBindReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(DefaultMessages.FAILED_TEST_SETUP)
    }

    LdapDistinguishedName exampleQueryBase() {
        return new LdapDistinguishedName().dn(server.getBaseDistinguishedName())
    }

    LdapQueryField exampleLdapQuery() {
        return ldapQuery("(objectClass=*)")
    }

    LdapQueryField ldapQuery(String query) {
        return new LdapQueryField().query(query)
    }

    LdapConnectionField startTlsLdapConnectionInfo() {
        return new LdapConnectionField()
                .hostname(server.getHostname())
                .port(server.getLdapPort())
                .encryptionMethod(LdapEncryptionMethodField.StartTlsEncryption.START_TLS)
    }

    LdapConnectionField ldapsLdapConnectionInfo() {
        return new LdapConnectionField()
                .hostname(server.getHostname())
                .port(server.getLdapSecurePort())
                .encryptionMethod(LdapEncryptionMethodField.LdapsEncryption.LDAPS)
    }

    LdapBindUserInfo digestBindInfo() {
        return new LdapBindUserInfo().bindMethod(LdapBindMethod.DigestMd5Sasl.DIGEST_MD5_SASL).username(server.getBasicAuthDn()).password(server.getBasicAuthPassword()).realm("testRealm")
    }
}
