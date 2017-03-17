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
package org.codice.ddf.admin.query.ldap.sample;

import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationField;
import org.codice.ddf.admin.query.ldap.fields.LdapConnectionField;
import org.codice.ddf.admin.query.ldap.fields.LdapCredentialsField;
import org.codice.ddf.admin.query.ldap.fields.LdapSettingsField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeField;

public class SampleFields {

    public static final LdapAttributeField SAMPLE_LDAP_ATTRIBUTE = new LdapAttributeField();
    static {
        SAMPLE_LDAP_ATTRIBUTE.setValue("exampleAttri");
    }

    public static final LdapConnectionField SAMPLE_LDAP_CONNECTION = new LdapConnectionField()
            .hostname("sampleHostName")
            .port(666);
    // TODO: tbatie - 2/22/17 - Need to figure out a clean way to do enums
    // .encryptionMethod(new LdapEncryptionMethodField(NONE))

    public static final LdapCredentialsField SAMPLE_LDAP_CREDENTIALS = new LdapCredentialsField()
            .username("sampleUserName")
            .password("samplePassword");

    public static final LdapSettingsField SAMPLE_LDAP_SETTINGS = new LdapSettingsField().userBaseDn("exampleUserBaseDn")
            .groupBaseDn("exampleBaseDn")
            .groupObjectClass("exampleGroupObjectClass")
            .groupMembershipAttribute("exampleMembershipAttribute")
            .usernameAttribute("exampleUsernameAttribute")
            .mappingEntry("exampleClaim", "exampleAttribute")
            .mappingEntry("exampleClaim2", "exampleAttribute2");

    public static final LdapConfigurationField SAMPLE_LDAP_CONFIGURATION = new LdapConfigurationField()
            .connection(SAMPLE_LDAP_CONNECTION)
            .credentials(SAMPLE_LDAP_CREDENTIALS)
            .settings(SAMPLE_LDAP_SETTINGS);
}
