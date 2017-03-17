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
