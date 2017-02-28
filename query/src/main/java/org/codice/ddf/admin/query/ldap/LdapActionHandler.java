package org.codice.ddf.admin.query.ldap;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapConfigurations;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapQuery;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapRecommendedSettings;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestBindField;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestConnectionField;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestSettingsField;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapUserAttributes;
import org.codice.ddf.admin.query.ldap.actions.persist.SaveLdapConfiguration;

public class LdapActionHandler extends DefaultActionHandler {

    public static final String FIELD_NAME = "ldap";
    public static final String DESCRIPTION = "Facilities for interacting with LDAP servers.";

    public LdapActionHandler() {
        super(FIELD_NAME, DESCRIPTION);
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return Arrays.asList(new LdapRecommendedSettings(),
                new LdapTestConnectionField(),
                new LdapTestBindField(),
                new LdapTestSettingsField(),
                new LdapQuery(),
                new LdapUserAttributes(),
                new LdapConfigurations());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return Arrays.asList(new SaveLdapConfiguration());
    }
}
