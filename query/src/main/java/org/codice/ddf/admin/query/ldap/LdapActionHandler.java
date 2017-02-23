package org.codice.ddf.admin.query.ldap;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapConfigurations;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapQuery;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapRecommendedSettings;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestBind;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestConnection;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapTestSettings;
import org.codice.ddf.admin.query.ldap.actions.discover.LdapUserAttributes;
import org.codice.ddf.admin.query.ldap.actions.persist.SaveLdapConfiguration;

public class LdapActionHandler extends DefaultActionHandler {

    public static final String ACTION_HANDLER_ID = "ldap";
    public static final String ACTION_HANDLER_DESCRIPTION = "Facilities for interacting with LDAP servers.";

    @Override
    public String getActionHandlerId() {
        return ACTION_HANDLER_ID;
    }

    @Override
    public String description() {
        return ACTION_HANDLER_DESCRIPTION;
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return Arrays.asList(new LdapRecommendedSettings(),
                new LdapTestConnection(),
                new LdapTestBind(),
                new LdapTestSettings(),
                new LdapQuery(),
                new LdapUserAttributes(),
                new LdapConfigurations());
    }

    @Override
    public List<Action> getPersistActions() {
        return Arrays.asList(new SaveLdapConfiguration());
    }
}
