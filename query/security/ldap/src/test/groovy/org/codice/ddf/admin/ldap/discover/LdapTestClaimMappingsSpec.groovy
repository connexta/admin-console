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
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.*
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE

class LdapTestClaimMappingsSpec extends Specification {
    static TestLdapServer server
    LdapTestClaimMappings action
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

        action = new LdapTestClaimMappings()

        // Initialize bad paths
        baseMsg = [LdapTestClaimMappings.FIELD_NAME, FunctionField.ARGUMENT]
        badPaths = [missingHostPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME],
                    missingPortPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME],
                    missingEncryptPath     : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME],
                    missingUsernamePath    : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                    missingUserpasswordPath: baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                    missingBindMethodPath  : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                    missingUserPath        : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_USER_DN],
                    missingUserNameAttrPath: baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.USER_NAME_ATTRIBUTE],
                    badClaimMappingPath    : baseMsg + [LdapConfigurationField.DEFAULT_FIELD_NAME, LdapConfigurationField.CLAIM_MAPPING]
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

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue(),
                (LdapConfigurationField.DEFAULT_FIELD_NAME)    : createClaimsMapping(ImmutableMap.of("foobar", "cn")).getValue()]
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

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue(),
                (LdapConfigurationField.DEFAULT_FIELD_NAME)    : createClaimsMapping(ImmutableMap.of("foobar", "cn")).getValue()]

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

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().password('badPassword').getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue(),
                (LdapConfigurationField.DEFAULT_FIELD_NAME)    : createClaimsMapping(ImmutableMap.of("foobar", "cn")).getValue()]

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

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue(),
                (LdapConfigurationField.DEFAULT_FIELD_NAME)    : createClaimsMapping(ImmutableMap.of("foobar", "cn")).getValue()]

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

        def failedPaths = [badPaths.badClaimMappingPath + (ListFieldImpl.INDEX_DELIMETER + 0) + ClaimsMapEntry.VALUE_FIELD_NAME,
                           badPaths.badClaimMappingPath + (ListFieldImpl.INDEX_DELIMETER + 2) + ClaimsMapEntry.VALUE_FIELD_NAME] as Set

        def claimsMapping = createClaimsMapping(ImmutableMap.of("claim1", "XXX",
                "claim2", "cn",
                "claim3", "YYY",
                "claim4", "employeetype"))
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue(),
                (LdapConfigurationField.DEFAULT_FIELD_NAME)    : claimsMapping.getValue()]

        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().size() == 2
        report.messages().count {
            it.getCode() == LdapMessages.USER_ATTRIBUTE_NOT_FOUND
        } == 2

        report.messages()*.path as Set == failedPaths
    }

    def 'pass when all user attributes found'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION_AND_ATTRIBUTE_STORE, true)
        def claimsMapping = createClaimsMapping(ImmutableMap.of("claim1", "sn",
                "claim2", "cn",
                "claim3", "employeetype"))


        def failedPaths = [badPaths.badClaimMappingPath + (ListFieldImpl.INDEX_DELIMETER + 0) + ClaimsMapEntry.VALUE_FIELD_NAME,
                           badPaths.badClaimMappingPath + (ListFieldImpl.INDEX_DELIMETER + 2) + ClaimsMapEntry.VALUE_FIELD_NAME] as Set

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue(),
                (LdapConfigurationField.DEFAULT_FIELD_NAME)    : claimsMapping.getValue()]

        action.setValue(args)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.getValue()

        then:
        report.messages().empty
        report.result().getValue()
    }

    private LdapConfigurationField createClaimsMapping(Map<String, String> claims) {
        return new LdapConfigurationField().mapAllClaims(claims)
    }
}
