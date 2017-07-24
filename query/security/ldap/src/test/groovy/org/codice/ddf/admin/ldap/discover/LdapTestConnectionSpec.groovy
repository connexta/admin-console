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
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.LdapTestingCommons
import org.codice.ddf.admin.ldap.TestLdapServer
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.forgerock.opendj.ldap.SSLContextBuilder
import org.forgerock.opendj.ldap.TrustManagers
import spock.lang.Specification

import javax.net.ssl.SSLContext
import java.security.GeneralSecurityException
import java.security.NoSuchAlgorithmException

import static org.codice.ddf.admin.ldap.LdapTestingCommons.noEncryptionLdapConnectionInfo
import static org.junit.Assert.fail

class LdapTestConnectionSpec extends Specification {
    static TestLdapServer server
    Map<String, Object> args
    LdapTestConnection ldapConnectFunction
    LdapTestingUtils utilsMock
    boolean ldapConnectionIsClosed

    def setupSpec() {
        server = TestLdapServer.getInstance()
        server.startListening()
    }

    def cleanupSpec() {
        server.shutdown()
        server = null
    }

    def setup() {
        utilsMock = new LdapTestingUtilsMock()
        LdapTestingCommons.loadLdapTestProperties()
        ldapConnectFunction = new LdapTestConnection()
    }

    def cleanup() {
        ldapConnectionIsClosed = false
    }

    def 'Fail on missing required connection info fields'() {
        setup:
        def baseMsg = [LdapTestConnection.FIELD_NAME, FunctionField.ARGUMENT]
        def missingHostMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME]
        def missingPortMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME]
        def missingEncryptMsgPath = baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME]

        ldapConnectFunction = new LdapTestConnection()

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().size() == 3
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 3

        report.messages()*.getPath() as Set == [missingHostMsgPath, missingPortMsgPath, missingEncryptMsgPath] as Set
    }

    def 'Successfully connect without encryption'() {
        setup:
        utilsMock = new LdapTestingUtilsMock()
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue()]
        ldapConnectFunction.setValue(args)
        ldapConnectFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapConnectFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().result().isClosed()

        then:
        report.messages().empty
        report.result().getValue()
        ldapConnectionIsClosed
    }

    def 'Successfully connect using LDAPS protocol'() {
        setup:
        utilsMock = new LdapTestingUtilsMock()
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().getValue()]
        ldapConnectFunction.setValue(args)
        ldapConnectFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapConnectFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().result().isClosed()

        then:
        report.messages().empty
        report.result().getValue()
        ldapConnectionIsClosed
    }

    def 'Successfully connect using startTls on standard port'() {
        setup:
        utilsMock = new LdapTestingUtilsMock()
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): startTlsLdapConnectionInfo(server.getLdapPort()).getValue()]
        ldapConnectFunction.setValue(args)
        ldapConnectFunction.setTestingUtils(utilsMock)

        when:
        FunctionReport report = ldapConnectFunction.getValue()
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().result().isClosed()

        then:
        report.messages().isEmpty()
        report.result().getValue()
        ldapConnectionIsClosed
    }

    def 'Fail to startTls over LDAPS port'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): startTlsLdapConnectionInfo(server.getLdapSecurePort()).getValue()]
        ldapConnectFunction.setValue(args)
        ldapConnectFunction.setTestingUtils(new LdapTestingUtilsMock())

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().size() == 1
        !report.result().getValue()
        report.messages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages().get(0).getPath() == [LdapTestConnection.FIELD_NAME, FunctionField.ARGUMENT, LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'Fail to connect to LDAP (Bad hostname)'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().hostname("badHostname").getValue()]
        ldapConnectFunction.setValue(args)

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().size() == 1
        !report.result().getValue()
        report.messages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages().get(0).getPath() == [LdapTestConnection.FIELD_NAME, FunctionField.ARGUMENT, LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'Fail to connect to LDAP (Bad port)'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().port(666).getValue()]
        ldapConnectFunction.setValue(args)

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().size() == 1
        !report.result().getValue()
        report.messages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages().get(0).getPath() == [LdapTestConnection.FIELD_NAME, FunctionField.ARGUMENT, LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'Fail to setup connection test'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().getValue()]
        ldapConnectFunction.setValue(args)
        ldapConnectFunction.setTestingUtils(new LdapTestingUtilsMock(true))

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().size() == 1
        !report.result().getValue()
        report.messages().get(0).getCode() == DefaultMessages.FAILED_TEST_SETUP
        report.messages().get(0).getPath() == [LdapTestConnection.FIELD_NAME]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        LdapTestConnection cannotConnectLdapFunc = new LdapTestConnection()
        Map<String, Object> cannotConnectArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().hostname("badHostname").getValue()]
        cannotConnectLdapFunc.setValue(cannotConnectArgs)

        LdapTestConnection failedSetupLdapFunc = new LdapTestConnection()
        Map<String, Object> failedSetupArgs = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().getValue()]
        failedSetupLdapFunc.setValue(failedSetupArgs)
        failedSetupLdapFunc.setTestingUtils(new LdapTestingUtilsMock(true))

        when:
        def errorCodes = ldapConnectFunction.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectLdapFunc.getValue()
        def failedSetupReport = failedSetupLdapFunc.getValue()

        then:
        errorCodes.size() == 2
        errorCodes.contains(cannotConnectReport.messages().get(0).getCode())
        errorCodes.contains(failedSetupReport.messages().get(0).getCode())
    }

    LdapConnectionField ldapsLdapConnectionInfo() {
        return new LdapConnectionField()
                .hostname(server.getHostname())
                .port(server.getLdapSecurePort())
                .encryptionMethod(LdapEncryptionMethodField.LdapsEncryption.LDAPS)
    }

    LdapConnectionField startTlsLdapConnectionInfo(int port) {
        return new LdapConnectionField()
                .hostname(server.getHostname())
                .port(port)
                .encryptionMethod(LdapEncryptionMethodField.StartTlsEncryption.START_TLS)
    }

    /**
     * Overrides the client context to accept untrusted certs
     */
    static class LdapTestingUtilsMock extends LdapTestingUtils {

        def connectionAttempt

        private boolean throwException

        LdapTestingUtilsMock(boolean throwSSLContextExcp) {
            super()
            this.throwException = throwSSLContextExcp
        }

        LdapTestingUtilsMock() {
            this(false)
        }

        @Override
        LdapConnectionAttempt getLdapConnection(LdapConnectionField connection) {
            connectionAttempt = super.getLdapConnection(connection)
            return connectionAttempt
        }

        @Override
        SSLContext getSslContext() throws NoSuchAlgorithmException {
            if (throwException) {
                throw new RuntimeException("Throwing error for " + LdapTestingUtilsMock.class + ".")
            }
            try {
                return new SSLContextBuilder().setTrustManager(TrustManagers.trustAll())
                        .getSSLContext()
            } catch (GeneralSecurityException e) {
                fail(e.getMessage())
                return null
            }
        }

        LdapConnectionAttempt getLdapConnectionAttempt() {
            return connectionAttempt
        }
    }
}
