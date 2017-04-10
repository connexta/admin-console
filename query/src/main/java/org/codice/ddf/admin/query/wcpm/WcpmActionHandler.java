package org.codice.ddf.admin.query.wcpm;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.BaseActionHandler;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetAuthTypes;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetContextPolicies;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetRealms;
import org.codice.ddf.admin.query.wcpm.actions.discover.GetWhiteListContexts;
import org.codice.ddf.admin.query.wcpm.actions.persist.SaveContextPolices;
import org.codice.ddf.admin.query.wcpm.actions.persist.SaveWhitelistedContexts;

import com.google.common.collect.ImmutableList;

public class WcpmActionHandler extends BaseActionHandler {
    public static final String FIELD_NAME = "wcpm";
    public static final String FIELD_TYPE_NAME = "WebContextPolicyManager";
    public static final String DESCRIPTION = "Manages policies for the system's endpoints";

    public WcpmActionHandler() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return ImmutableList.of(new GetAuthTypes(), new GetRealms(), new GetWhiteListContexts(), new GetContextPolicies());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return ImmutableList.of(new SaveContextPolices(), new SaveWhitelistedContexts());
    }
}
