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
import org.codice.ddf.admin.ldap.TestLdapServer
import org.codice.ddf.admin.ldap.commons.LdapMessages
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.*
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AttributeStore.ATTRIBUTE_STORE
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AuthenticationEnumValue.AUTHENTICATION

class LdapTestDirectorySettingsSpec extends Specification {
    static final List<Object> FUNCTION_PATH = [LdapTestDirectorySettings.FIELD_NAME]
    static TestLdapServer server
    Map<String, Object> args
    LdapTestDirectorySettings action
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
        loadLdapTestProperties()
        action = new LdapTestDirectorySettings()

        // Initialize bad paths
        baseMsg = [LdapTestDirectorySettings.FIELD_NAME]
        badPaths = [missingHostPath            : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME],
                    missingPortPath            : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME],
                    missingEncryptPath         : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME],
                    missingUsernamePath        : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                    missingUserpasswordPath    : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                    missingBindMethodPath      : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                    missingUseCasePath         : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapUseCase.DEFAULT_FIELD_NAME],
                    missingUserPath            : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_USER_DN],
                    missingGroupPath           : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_GROUP_DN],
                    missingLoginUserAttrPath    : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.LOGIN_USER_ATTRIBUTE],
                    missingGroupObjectPath     : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_OBJECT_CLASS],
                    missingGroupAttribPath     : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_ATTRIBUTE_HOLDING_MEMBER],
                    missingMemberAttribPath    : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP],
                    badUserDnPath              : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_USER_DN],
                    badGroupDnPath             : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_GROUP_DN],
                    badLoginUserAttribFormatPath: baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.LOGIN_USER_ATTRIBUTE],
                    badGroupAttribFormatPath   : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_ATTRIBUTE_HOLDING_MEMBER],
                    badMemberAttribFormatPath  : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP]
        ]
    }

    def cleanup() {
        ldapConnectionIsClosed = false
    }

    def 'fail on missing required fields'() {
        setup:
        action = new LdapTestDirectorySettings()

        when:
        FunctionReport report = action.execute(null, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 10
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 10

        report.getErrorMessages()*.getPath() as Set == [badPaths.missingHostPath,
                                                badPaths.missingPortPath,
                                                badPaths.missingEncryptPath,
                                                badPaths.missingUsernamePath,
                                                badPaths.missingUserpasswordPath,
                                                badPaths.missingBindMethodPath,
                                                badPaths.missingUseCasePath,
                                                badPaths.missingUserPath,
                                                badPaths.missingGroupPath,
                                                badPaths.missingLoginUserAttrPath] as Set
    }

    def 'fail on missing required fields for LDAP Attribute Store'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE)

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 3
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 3
        report.getErrorMessages()*.getPath() as Set == [badPaths.missingGroupObjectPath,
                                                badPaths.missingGroupAttribPath,
                                                badPaths.missingMemberAttribPath] as Set
    }

    def 'fail with invalid user/group dn'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .baseUserDn('BAD')
                .baseGroupDn('BAD')

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.INVALID_DN
        } == 2

        report.getErrorMessages()*.getPath() as Set == [badPaths.badUserDnPath,
                                                badPaths.badGroupDnPath] as Set
    }

    def 'fail to connect to LDAP'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION)

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'fail to bind to LDAP'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION)

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().password('badPassword').getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'fail when baseUserDN & baseGroupDN do not exist'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .baseUserDn('ou=users,dc=example,dc=BAD')
                .baseGroupDn('ou=groups,dc=example,dc=BAD')

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.DN_DOES_NOT_EXIST
        } == 2

        report.getErrorMessages()*.getPath() as Set == [badPaths.badUserDnPath,
                                                badPaths.badGroupDnPath] as Set
    }

    def 'fail to find entries in baseGroupDn'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .baseGroupDn('ou=emptygroups,dc=example,dc=com')

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(utilsMock)

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.NO_GROUPS_IN_BASE_GROUP_DN
        } == 2

        report.getErrorMessages()*.getPath() as Set == [badPaths.badGroupDnPath,
                                                badPaths.missingGroupObjectPath] as Set
        ldapConnectionIsClosed
    }

    def 'fail to find specified groupObjectClass in groups'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .groupObjectClass('BADOBJECTCLASS')

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(utilsMock)

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.NO_GROUPS_IN_BASE_GROUP_DN
        } == 2

        report.getErrorMessages()*.getPath() as Set == [badPaths.badGroupDnPath,
                                                badPaths.missingGroupObjectPath] as Set
        ldapConnectionIsClosed
    }

    def 'fail to find entries in baseUserDN'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .baseUserDn('ou=emptyusers,dc=example,dc=com')

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(utilsMock)

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.NO_USERS_IN_BASE_USER_DN
        } == 1
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.USER_ATTRIBUTE_NOT_FOUND
        } == 1

        report.getErrorMessages()*.getPath() as Set == [badPaths.badUserDnPath,
                                                badPaths.missingLoginUserAttrPath] as Set
        ldapConnectionIsClosed
    }

    def 'fail when the loginUserAttribute format is incorrect'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .loginUserAttribute("space & speci@l ch@r@cters")

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.INVALID_USER_ATTRIBUTE
        } == 1

        report.getErrorMessages()*.getPath() as Set == [badPaths.badLoginUserAttribFormatPath] as Set
    }

    def 'fail when the groupAttributeHoldingMember format is incorrect'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .groupAttributeHoldingMember("sp&ci@!Ch@r@cters")

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.INVALID_USER_ATTRIBUTE
        } == 1

        report.getErrorMessages()*.getPath() as Set == [badPaths.badGroupAttribFormatPath] as Set
    }

    def 'fail when the memberAttributeReferencedInGroup format is incorrect'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
                .memberAttributeReferencedInGroup("space with sp&ci@! ch@r@cters")

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.INVALID_USER_ATTRIBUTE
        } == 1

        report.getErrorMessages()*.getPath() as Set == [badPaths.badMemberAttribFormatPath] as Set
    }

    def 'succeed'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        action.setTestingUtils(utilsMock)

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().empty
        report.getResult().getValue()
        ldapConnectionIsClosed
    }

    def 'When useCase = AttributeStore, checkGroupObjectClass, checkGroup, and checkReferencedUser should be applied'() {
        setup:
        def ldapSettings = initLdapSettings(ATTRIBUTE_STORE, true)
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        def action = Spy(LdapTestDirectorySettings)
        action.setTestingUtils(utilsMock)

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        1 * action.checkUsersInDir(_)
        1 * action.checkGroupObjectClass(_)
        1 * action.checkGroup(_)
        1 * action.checkReferencedUser(_, _)
    }

    def 'When useCase = Authentication, checkGroupObjectClass, checkGroup, and checkReferencedUser should be applied'() {
        setup:
        def ldapSettings = initLdapSettings(AUTHENTICATION, true)
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)       : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)          : simpleBindInfo().getValue(),
                (LdapDirectorySettingsField.DEFAULT_FIELD_NAME): ldapSettings.getValue()]
        def action = Spy(LdapTestDirectorySettings)
        action.setTestingUtils(utilsMock)

        when:
        action.execute(args, FUNCTION_PATH)

        then:
        1 * action.checkUsersInDir(_)
        0 * action.checkGroupObjectClass(_)
        0 * action.checkGroup(_)
        0 * action.checkReferencedUser(_, _)
    }

    def 'Returns all the possible error codes correctly'(){
        when:
        def errorCodes = action.getFunctionErrorCodes()

        then:
        errorCodes.size() == 9
        errorCodes.contains(DefaultMessages.CANNOT_CONNECT)
        errorCodes.contains(DefaultMessages.FAILED_TEST_SETUP)
        errorCodes.contains(LdapMessages.USER_ATTRIBUTE_NOT_FOUND)
        errorCodes.contains(LdapMessages.DN_DOES_NOT_EXIST)
        errorCodes.contains(LdapMessages.CANNOT_BIND)
        errorCodes.contains(LdapMessages.NO_USERS_IN_BASE_USER_DN)
        errorCodes.contains(LdapMessages.NO_GROUPS_IN_BASE_GROUP_DN)
        errorCodes.contains(LdapMessages.NO_GROUPS_WITH_MEMBERS)
        errorCodes.contains(LdapMessages.NO_REFERENCED_MEMBER)
    }
}
