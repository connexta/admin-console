package org.codice.ddf.admin.query.ldap.actions;

import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.ldap.fields.LdapSettingsField;

public class LdapRecommendedSettings extends GetAction<LdapSettingsField> {

    public static final String ACTION_ID = "recommendedSettings";
    public static final String DESCRIPTION = "Attempts to retrieve recommended settings from the LDAP connection.";

    public LdapRecommendedSettings() {
        super(ACTION_ID, DESCRIPTION, new LdapSettingsField());
    }

    @Override
    public LdapSettingsField process() {
        LdapSettingsField settings = new LdapSettingsField();
        settings.setGroupBaseDn("exampleBaseDn");
        settings.setGroupMembershipAttribute("exampleMembershipAttribute");
        settings.setGroupObjectClass("exampleGroupObjectClass");
        return settings;
    }
}
