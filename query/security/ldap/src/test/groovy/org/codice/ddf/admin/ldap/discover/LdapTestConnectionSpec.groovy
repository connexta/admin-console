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
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.forgerock.opendj.ldap.SSLContextBuilder
import org.forgerock.opendj.ldap.TrustManagers
import org.junit.Ignore
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

    def setupSpec() {
        server = TestLdapServer.getInstance()
        server.startListening()
    }

    def cleanupSpec() {
        server.shutdown()
        server = null
    }

    def setup() {
        LdapTestingCommons.loadLdapTestProperties()
        ldapConnectFunction = new LdapTestConnection()
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
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue()]
        ldapConnectFunction.setValue(args)

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().empty
        report.result().getValue()
    }

    def 'Successfully connect using LDAPS protocol'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): ldapsLdapConnectionInfo().getValue()]
        ldapConnectFunction.setValue(args)
        ldapConnectFunction.setTestingUtils(new LdapTestingUtilsMock())

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().empty
        report.result().getValue()
    }

    // TODO: tbatie - 5/4/17 - need to figure out a way to check if the connection was successfully upgraded to TLS or has no encryption. This information should be relayed back to the user.
    @Ignore
    def 'Successfully connect using startTls on insecure port (Should not upgrade)'() {
    }

    def 'Successfully connect using startTls on LDAPS port (Should upgrade)'() {
        setup:
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): startTlsLdapConnectionInfo(server.getLdapSecurePort()).getValue()]
        ldapConnectFunction.setValue(args)
        ldapConnectFunction.setTestingUtils(new LdapTestingUtilsMock())

        when:
        FunctionReport report = ldapConnectFunction.getValue()

        then:
        report.messages().isEmpty()
        report.result().getValue()
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

    LdapConnectionField ldapsLdapConnectionInfo() {
        return new LdapConnectionField()
                .hostname(server.getHostname())
                .port(server.getLdapSecurePort())
                .encryptionMethod(LdapEncryptionMethodField.LDAPS)
    }

    LdapConnectionField startTlsLdapConnectionInfo(int port) {
        return new LdapConnectionField()
                .hostname(server.getHostname())
                .port(port)
                .encryptionMethod(LdapEncryptionMethodField.START_TLS)
    }

    /**
     * Overrides the client context to accept untrusted certs
     */
    static class LdapTestingUtilsMock extends LdapTestingUtils {

        private boolean throwException

        LdapTestingUtilsMock(boolean throwSSLContextExcp) {
            super()
            this.throwException = throwSSLContextExcp
        }

        LdapTestingUtilsMock() {
            this(false)
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
    }
}
