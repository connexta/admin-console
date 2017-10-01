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
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.LdapTestingCommons
import org.codice.ddf.admin.ldap.TestLdapServer
import org.codice.ddf.admin.ldap.commons.LdapMessages
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils
import org.codice.ddf.admin.ldap.fields.connection.*
import spock.lang.Ignore
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.noEncryptionLdapConnectionInfo
import static org.codice.ddf.admin.ldap.LdapTestingCommons.simpleBindInfo

class LdapTestBindSpec extends Specification {
    static final List<Object> FUNCTION_PATH = [LdapTestBind.FUNCTION_NAME]
    static TestLdapServer server
    Map<String, Object> args
    LdapTestBind ldapBindFunction
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
        ldapBindFunction = new LdapTestBind()
    }

    def cleanup() {
        ldapConnectionIsClosed = false
    }

    def 'Fail on missing required fields'() {
        setup:
        def baseMsg = [LdapTestBind.FUNCTION_NAME]
        def missingHostMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME]
        def missingPortMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME]
        def missingEncryptMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME]
        def missingUsernameMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME]
        def missingUserpasswordMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME]
        def missingBindMethodMsgPath = baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME]

        when:
        FunctionReport report = ldapBindFunction.execute(null, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 6
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 6

        report.getErrorMessages()*.getPath() as Set == [missingHostMsgPath, missingPortMsgPath, missingEncryptMsgPath,
                                                missingUsernameMsgPath, missingUserpasswordMsgPath, missingBindMethodMsgPath] as Set
    }

    def 'Fail on missing bind realm'() {
        setup:
        args = [(LdapBindUserInfo.DEFAULT_FIELD_NAME)   : digestBindInfo().realm(null).getValue(),
                (LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue()]

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        report.getErrorMessages().get(0).getPath() == [LdapTestBind.FUNCTION_NAME, LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapRealm.DEFAULT_FIELD_NAME]
    }

    def 'Fail to connect to LDAP'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue()]

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.getErrorMessages().get(0).getPath() == [LdapTestBind.FUNCTION_NAME, LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'Successfully bind user with no encryption using bind method "Simple"'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue()]
        ldapBindFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().empty
        report.getResult().getValue()
        ldapConnectionIsClosed
    }

    def 'Successfully bind user with LDAPS encryption using bind method "Simple"'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue()]
        ldapBindFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().empty
        report.getResult().getValue()
        ldapConnectionIsClosed
    }

    def 'Fail to bind user with no encryption using bind method "DigestMD5SASL"'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : digestBindInfo().getValue()]

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == LdapMessages.MD5_NEEDS_ENCRYPTED
        report.getErrorMessages().get(0).getPath() == [LdapTestBind.FUNCTION_NAME,
                                                       LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME]
    }

    @Ignore
    // TODO: tbatie - 5/4/17 - Need to make in memory ldap support DigestMD5
    def 'Successfully bind user with LDAPS encryption using bind method "DigestMD5SASL"'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : digestBindInfo().getValue()]
        ldapBindFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().empty
        report.getResult().getValue()
        ldapConnectionIsClosed
    }

    @Ignore
    // TODO RAP 10 Jul 17: Need o make in-memory ldap support DigestMD5
    def 'Fail to bind user using bind method "DigestMD5SASL" with bad realm'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : digestBindInfo().getValue()]
        ldapBindFunction.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == [LdapTestBind.FUNCTION_NAME, LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'Fail to bind user over bind method "Simple" with bad password'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password("badPassword").getValue()]

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == [LdapTestBind.FUNCTION_NAME, LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'Fail to bind user over bind method "Simple" with bad username'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().username("badUsername").getValue()]

        when:
        FunctionReport report = ldapBindFunction.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == [LdapTestBind.FUNCTION_NAME, LdapBindUserInfo.DEFAULT_FIELD_NAME]
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

    def 'Returns all the possible error codes correctly'(){
        setup:
        LdapTestBind cannotConnectBindFunc = new LdapTestBind()
        Map<String, Object> cannotConnectArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue()]

        LdapTestBind cannotBindFunc = new LdapTestBind()
        Map<String, Object> cannotBindArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : digestBindInfo().getValue()]

        LdapTestBind md5NeededBindFunc = new LdapTestBind()
        md5NeededBindFunc.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        def errorCodes = ldapBindFunction.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectBindFunc.execute(cannotConnectArgs, FUNCTION_PATH)
        def cannotBindReport = cannotBindFunc.execute(cannotBindArgs, FUNCTION_PATH)
        def md5NeededReport = md5NeededBindFunc.execute(cannotBindArgs, FUNCTION_PATH)

        then:
        errorCodes.size() == 4
        errorCodes.contains(cannotConnectReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(cannotBindReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(md5NeededReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(DefaultMessages.FAILED_TEST_SETUP)
    }
}
