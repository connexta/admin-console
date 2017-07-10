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
package org.codice.ddf.admin.ldap;

import java.io.IOException;
import java.util.Properties;

import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField;

public class LdapTestingCommons {

    public static final String LDAP_SERVER_BASE_USER_DN = "ou=users,dc=example,dc=com";

    public static final String LDAP_SERVER_BASE_GROUP_DN = "ou=groups,dc=example,dc=com";

    public static void loadLdapTestProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(LdapTestingCommons.class.getClassLoader()
                .getResourceAsStream("test.properties"));
        properties.forEach((o, o2) -> System.setProperty((String) o, (String) o2));
    }

    public static LdapConnectionField noEncryptionLdapConnectionInfo() {
        return new LdapConnectionField().hostname(TestLdapServer.getHostname())
                .port(TestLdapServer.getLdapPort())
                .encryptionMethod(LdapEncryptionMethodField.NoEncryption.NONE);
    }

    public static LdapBindUserInfo simpleBindInfo() {
        return new LdapBindUserInfo().bindMethod(LdapBindMethod.Simple.SIMPLE)
                .username(TestLdapServer.getBasicAuthDn())
                .password(TestLdapServer.getBasicAuthPassword());
    }

    public static LdapDirectorySettingsField initLdapSettings(String useCase) {
        return initLdapSettings(useCase, false);
    }

    public static LdapDirectorySettingsField initLdapSettings(String useCase,
            boolean includeAttributeFields) {
        LdapDirectorySettingsField settingsField =
                new LdapDirectorySettingsField().usernameAttribute("sn")
                .baseUserDn(LDAP_SERVER_BASE_USER_DN)
                .baseGroupDn(LDAP_SERVER_BASE_GROUP_DN)
                .useCase(useCase);

        if (includeAttributeFields) {
            settingsField.groupObjectClass("groupOfNames")
                    .groupAttributeHoldingMember("member")
                    .memberAttributeReferencedInGroup("uid");
        }

        return settingsField;
    }
}
