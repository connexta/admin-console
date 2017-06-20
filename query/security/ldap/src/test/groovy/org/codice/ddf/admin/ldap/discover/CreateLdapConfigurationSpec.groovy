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
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField
import org.codice.ddf.admin.ldap.fields.config.LdapUseCase
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.ldap.persist.CreateLdapConfiguration
import spock.lang.Specification

class CreateLdapConfigurationSpec extends Specification {
    def badPaths
    def baseMsg
    def createConfigFunc

    def setup() {
        // Initialize bad paths
        baseMsg = [CreateLdapConfiguration.FIELD_NAME, FunctionField.ARGUMENT, LdapConfigurationField.DEFAULT_FIELD_NAME]
        badPaths = [missingHostPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME],
                    missingPortPath        : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME],
                    missingEncryptPath     : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME],
                    missingUsernamePath    : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                    missingUserpasswordPath: baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                    missingBindMethodPath  : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                    missingUseCasePath     : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapUseCase.DEFAULT_FIELD_NAME],
                    missingUserPath        : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_USER_DN],
                    missingGroupPath       : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_GROUP_DN],
                    missingUserNameAttrPath: baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.USER_NAME_ATTRIBUTE],
                    missingGroupObjectPath : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_OBJECT_CLASS],
                    missingGroupAttribPath : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.GROUP_ATTRIBUTE_HOLDING_MEMBER],
                    missingMemberAttribPath: baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP],
                    badUserDnPath          : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_USER_DN],
                    badGroupDnPath         : baseMsg + [LdapDirectorySettingsField.DEFAULT_FIELD_NAME, LdapDirectorySettingsField.BASE_GROUP_DN]

        ]
    }

    def 'fail on missing required fields'() {
        setup:
        createConfigFunc = new CreateLdapConfiguration(null, null, null, null)

        when:
        FunctionReport report = createConfigFunc.getValue()

        then:
        report.messages().size() == 10
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 10

        report.messages()*.getPath() as Set == [badPaths.missingHostPath,
                                                badPaths.missingPortPath,
                                                badPaths.missingEncryptPath,
                                                badPaths.missingUsernamePath,
                                                badPaths.missingUserpasswordPath,
                                                badPaths.missingBindMethodPath,
                                                badPaths.missingUseCasePath,
                                                badPaths.missingUserPath,
                                                badPaths.missingGroupPath,
                                                badPaths.missingUserNameAttrPath] as Set
    }
}
