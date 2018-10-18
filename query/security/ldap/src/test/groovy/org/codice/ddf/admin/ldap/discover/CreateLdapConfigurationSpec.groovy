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
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.ldap.persist.CreateLdapConfiguration
import org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase
import spock.lang.Specification

class CreateLdapConfigurationSpec extends Specification {

    static final List<Object> FUNCTION_PATH = [CreateLdapConfiguration.FIELD_NAME]

    def authBadPaths
    def attributeStoreBadPaths
    def baseMsg
    def createConfigFunc

    def setup() {
        // Initialize bad paths
        baseMsg = [CreateLdapConfiguration.FIELD_NAME, LdapConfigurationField.DEFAULT_FIELD_NAME]
        authBadPaths = [missingConnectionPath    : baseMsg + [LdapConnectionField.ListImpl.DEFAULT_FIELD_NAME],
                        missingUsernamePath: baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                        missingUserpasswordPath: baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                        missingBindMethodPath  : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                        missingUseCasePath     : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapUseCase.DEFAULT_FIELD_NAME],
                        missingUserPath        : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_USER_DN],
                        missingGroupPath       : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_GROUP_DN],
                        missingLoginUserAttrPath: baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.LOGIN_USER_ATTRIBUTE],
                        missingGroupObjectPath : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_OBJECT_CLASS],
                        missingGroupAttribPath : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_ATTRIBUTE_HOLDING_MEMBER],
                        missingMemberAttribPath: baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP],
                        badUserDnPath          : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_USER_DN],
                        badGroupDnPath     : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_GROUP_DN]

        ]

        attributeStoreBadPaths =
                [
                        missingGroupAttributeHoldingMemberPath     : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_ATTRIBUTE_HOLDING_MEMBER],
                        missingGroupObjectClassPath                : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_OBJECT_CLASS],
                        missingMemberAttributeReferencedInGroupPath: baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP],
                        missingClaimsMappingPath                   : baseMsg + [LdapConfigurationField.CLAIMS_MAPPING]
                ]
    }

    def 'fail on missing required fields for authentication'() {
        setup:
        createConfigFunc = new CreateLdapConfiguration(null)

        when:
        FunctionReport report = createConfigFunc.execute(null, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 8
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 8

        report.getErrorMessages()*.getPath() as Set == [authBadPaths.missingConnectionPath,
                                                authBadPaths.missingUsernamePath,
                                                authBadPaths.missingUserpasswordPath,
                                                authBadPaths.missingBindMethodPath,
                                                authBadPaths.missingUseCasePath,
                                                authBadPaths.missingUserPath,
                                                authBadPaths.missingGroupPath,
                                                authBadPaths.missingLoginUserAttrPath] as Set
    }

    def 'fail on missing required fields for attribute store'() {
        setup:
        createConfigFunc = new CreateLdapConfiguration(null)

        def configArg = new LdapConfigurationField().settings(new LdapDirectorySettingsField().useCase(LdapUseCase.AttributeStore.ATTRIBUTE_STORE))
        def args = [
                (LdapConfigurationField.DEFAULT_FIELD_NAME): configArg.getValue()
        ]

        when:
        FunctionReport report = createConfigFunc.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages()*.getPath() as Set == [authBadPaths.missingConnectionPath,
                                                authBadPaths.missingUsernamePath,
                                                authBadPaths.missingUserpasswordPath,
                                                authBadPaths.missingBindMethodPath,
                                                authBadPaths.missingUserPath,
                                                authBadPaths.missingGroupPath,
                                                authBadPaths.missingLoginUserAttrPath,
                                                attributeStoreBadPaths.missingGroupAttributeHoldingMemberPath,
                                                attributeStoreBadPaths.missingGroupObjectClassPath,
                                                attributeStoreBadPaths.missingMemberAttributeReferencedInGroupPath,
                                                attributeStoreBadPaths.missingClaimsMappingPath

        ] as Set
        report.getErrorMessages().size() == 11
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 11
    }
}
