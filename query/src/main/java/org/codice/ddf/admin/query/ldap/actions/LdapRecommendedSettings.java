package org.codice.ddf.admin.query.ldap.actions;

import org.codice.ddf.admin.query.commons.action.GetAction;
import org.codice.ddf.admin.query.ldap.LdapFields;

public class LdapRecommendedSettings extends GetAction<LdapFields.LdapSettings> {

    public static final String ACTION_ID = "recommendedSettings";
    public static final String DESCRIPTION = "Attempts to retrieve recommended settings from the LDAP connection.";

    public LdapRecommendedSettings() {
        super(ACTION_ID, DESCRIPTION, new LdapFields.LdapSettings());
    }

    @Override
    public LdapFields.LdapSettings process() {
        LdapFields.LdapSettings settings = new LdapFields.LdapSettings();
        settings.setGroupBaseDn("exampleBaseDn");
        settings.setGroupMembershipAttribute("exampleMembershipAttribute");
        settings.setGroupObjectClass("exampleGroupObjectClass");
        return settings;
    }
}
