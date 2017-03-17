package org.codice.ddf.admin.query.ldap.actions;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.action.Action;
import org.codice.ddf.admin.query.commons.actions.BaseActionCreator;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapConfigurations;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapQuery;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapRecommendedSettings;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestBindField;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestConnectionField;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestSettingsField;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapUserAttributes;
import org.codice.ddf.admin.query.ldap.actions.persist.SaveLdapConfiguration;

public class LdapActionCreator extends BaseActionCreator {

    public static final String NAME = "ldap";
    public static final String TYPE_NAME = "Ldap";
    public static final String DESCRIPTION = "Facilities for interacting with LDAP servers.";

    public LdapActionCreator() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return Arrays.asList(new LdapRecommendedSettings(),
                new LdapTestConnectionField(),
                new LdapTestBindField(),
                new LdapTestSettingsField(),
                new LdapQuery(),
                new LdapUserAttributes(),
                new LdapConfigurations());
    }

    @Override
    public List<Action> getPersistActions() {
        return Arrays.asList(new SaveLdapConfiguration());
    }
}
