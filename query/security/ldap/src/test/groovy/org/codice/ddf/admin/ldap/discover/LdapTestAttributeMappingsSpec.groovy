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

import com.google.common.collect.ImmutableMap
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.report.FunctionReport
import org.codice.ddf.admin.common.fields.base.ListFieldImpl
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.TestLdapServer
import org.codice.ddf.admin.ldap.commons.LdapMessages
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField
import org.codice.ddf.admin.ldap.fields.config.LdapUseCase
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.*
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE

class LdapTestAttributeMappingsSpec extends Specification {
    static TestLdapServer server
    LdapTestAttributeMappings action
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
        loadLdapTestProperties()

        action = new LdapTestAttributeMappings()

        // Initialize bad paths
        baseMsg = [LdapTestAttributeMappings.FIELD_NAME, FunctionField.ARGUMENT]
        badPaths = [missingHostPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME],
                    missingPortPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME],
                    missingEncryptPath     : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME],
                    missingUsernamePath    : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                    missingUserpasswordPath: baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                    missingBindMethodPath  : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                    missingUseCasePath     : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapUseCase.DEFAULT_FIELD_NAME],
                    missingUserPath        : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapSettingsField.BASE_USER_DN],
                    missingGroupPath       : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapSettingsField.BASE_GROUP_DN],
                    missingUserNameAttrPath: baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapSettingsField.USER_NAME_ATTRIBUTE],
                    missingGroupObjectPath : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapSettingsField.GROUP_OBJECT_CLASS],
                    missingGroupAttribPath : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapSettingsField.GROUP_ATTRIBUTE_HOLDING_MEMBER],
                    missingMemberAttribPath: baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapSettingsField.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP],
                    badAttribMappingPath   : baseMsg + [LdapSettingsField.DEFAULT_FIELD_NAME, LdapSettingsField.ATTRIBUTE_MAPPING]
        ]
    }

    def 'fail on missing required fields'() {
        setup:

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == badPaths.size()
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == badPaths.size()

        report.messages()*.path as Set == badPaths.values() as Set
    }

    def 'fail with invalid user dn'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION_AND_ATTRIBUTE_STORE, true)
                .baseUserDn('BAD')
                .attributeMapField(ImmutableMap.of("foobar", "cn"))

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == LdapMessages.INVALID_DN
        } == 1

        report.messages()*.getPath() as Set == [badPaths.missingUserPath] as Set
    }

    def 'fail to connect to LDAP'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION_AND_ATTRIBUTE_STORE, true)
                .attributeMapField(ImmutableMap.of("foobar", "cn"))

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 1
        !report.result().getValue()
        report.messages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages().get(0).getPath() == baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'fail to bind to LDAP'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION_AND_ATTRIBUTE_STORE, true)
                .attributeMapField(ImmutableMap.of("foobar", "cn"))

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().password('badPassword').getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 1
        !report.result().getValue()
        report.messages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.messages().get(0).getPath() == baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'fail when baseUserDN does not exist'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION_AND_ATTRIBUTE_STORE, true)
                .baseUserDn('ou=users,dc=example,dc=BAD')
                .attributeMapField(ImmutableMap.of("foobar", "cn"))

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == LdapMessages.DN_DOES_NOT_EXIST
        } == 1

        report.messages()*.path as Set == [badPaths.missingUserPath] as Set
    }

    def 'fail when missing user attributes are supplied for mapping'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION_AND_ATTRIBUTE_STORE, true)
                .attributeMapField(ImmutableMap.of("claim1", "XXX"))
                .attributeMapField(ImmutableMap.of("claim2", "cn"))
                .attributeMapField(ImmutableMap.of("claim3", "YYY"))
                .attributeMapField(ImmutableMap.of("claim4", "employeetype"))

        def failedPaths = [badPaths.badAttribMappingPath + (ListFieldImpl.INDEX_DELIMETER + 0) + ClaimsMapEntry.VALUE_FIELD_NAME,
                           badPaths.badAttribMappingPath + (ListFieldImpl.INDEX_DELIMETER + 2) + ClaimsMapEntry.VALUE_FIELD_NAME] as Set

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 2
        report.messages().count {
            it.getCode() == LdapMessages.MAPPING_ATTRIBUTE_NOT_FOUND
        } == 2

        report.messages()*.path as Set == failedPaths
    }

    def 'pass when all user attributes found'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION_AND_ATTRIBUTE_STORE, true)
                .attributeMapField(ImmutableMap.of("claim1", "sn"))
                .attributeMapField(ImmutableMap.of("claim2", "cn"))
                .attributeMapField(ImmutableMap.of("claim3", "employeetype"))

        def failedPaths = [badPaths.badAttribMappingPath + (ListFieldImpl.INDEX_DELIMETER + 0) + ClaimsMapEntry.VALUE_FIELD_NAME,
                           badPaths.badAttribMappingPath + (ListFieldImpl.INDEX_DELIMETER + 2) + ClaimsMapEntry.VALUE_FIELD_NAME] as Set

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME): noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)   : simpleBindInfo().getValue(),
                (LdapSettingsField.DEFAULT_FIELD_NAME)  : ldapSettings.getValue()]
        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().empty
        report.result().getValue()
    }
}
