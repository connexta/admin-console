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
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField
import org.codice.ddf.admin.ldap.fields.config.LdapUseCase
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.*
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE

class LdapUserAttributesSpec extends Specification {
    static TestLdapServer server
    LdapUserAttributes action
    Map<String, Object> args
    def badPaths
    def baseMsg

    def setupSpec() {
        server = TestLdapServer.getInstance().useSimpleAuth()
        server.startListening()
    }

    def cleanupSpec() {
        server.shutdown()
        server = null
    }

    def setup() {
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
                    useCasePath                         : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapUseCase.DEFAULT_FIELD_NAME],
                    baseUserDnPath                      : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, 'baseUserDn'],
                    baseGroupDnPath                     : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, 'baseGroupDn'],
                    groupObjectClassPath                : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, 'groupObjectClass'],
                    usernameAttributePath               : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, 'userNameAttribute'],
                    groupAttributeHoldingMemberPath     : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, 'groupAttributeHoldingMember'],
                    memberAttributeReferencedInGroupPath: baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, 'memberAttributeReferencedInGroup'],
                    attributeMappingPath                : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, 'attributeMapping']
        ]
    }

    def 'fail on missing required fields'() {
        setup:
        action = new LdapUserAttributes()

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): new LdapConnectionField().getValue()]
        action.setValue(args)

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == badPaths.size()
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == badPaths.size()

        report.messages()*.getPath() as Set == badPaths.values() as Set
    }

    def 'fail to connect to LDAP'() {
        setup:

        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 1
        report.messages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages().get(0).getPath() == baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'fail to bind to LDAP'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password('badPassword').getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 1
        report.messages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.messages().get(0).getPath() == baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'succeed'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        ListField<StringField> report = action.getValue().result()

        then:
        !report.list.empty
        report.list.any {
            it.value == 'cn'
        }
    }

}
