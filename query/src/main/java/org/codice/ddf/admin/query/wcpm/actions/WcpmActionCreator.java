package org.codice.ddf.admin.query.wcpm.actions;

import java.util.List;

import org.codice.ddf.admin.query.api.action.Action;
import org.codice.ddf.admin.query.commons.actions.BaseActionCreator;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetAuthTypes;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetContextPolicies;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetRealms;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetWhiteListContexts;
import org.codice.ddf.admin.query.wcpm.actions.persist.SaveContextPolices;
import org.codice.ddf.admin.query.wcpm.actions.persist.SaveWhitelistedContexts;

import com.google.common.collect.ImmutableList;

public class WcpmActionCreator extends BaseActionCreator {
    public static final String NAME = "wcpm";
    public static final String TYPE_NAME = "WebContextPolicyManager";
    public static final String DESCRIPTION = "Manages policies for the system's endpoints";

    public WcpmActionCreator() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new GetAuthTypes(), new GetRealms(), new GetWhiteListContexts(), new GetContextPolicies());
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of(new SaveContextPolices(), new SaveWhitelistedContexts());
    }
}
