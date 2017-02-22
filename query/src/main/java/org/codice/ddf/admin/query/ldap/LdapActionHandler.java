package org.codice.ddf.admin.query.ldap;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;
import org.codice.ddf.admin.query.ldap.actions.LdapRecommendedSettings;
import org.codice.ddf.admin.query.ldap.actions.LdapTestBind;
import org.codice.ddf.admin.query.ldap.actions.LdapTestConnection;
import org.codice.ddf.admin.query.ldap.actions.LdapTestSettings;

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
    public List<Action> getSupportedActions() {
        return Arrays.asList(new LdapRecommendedSettings(), new LdapTestConnection(), new LdapTestBind(), new LdapTestSettings());
    }
}
