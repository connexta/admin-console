package org.codice.ddf.admin.query.sts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;
import org.codice.ddf.admin.query.sts.actions.GetStsClaimsAction;

public class StsActionHandler extends DefaultActionHandler {

    public static final String ACTION_HANDLER_ID = "sts";

    public static final String DESCRIPTION = "The STS (Security Token Service) is responsible for generating assertions that allow clients to be authenticated.";

    @Override
    public String getActionHandlerId() {
        return ACTION_HANDLER_ID;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return Arrays.asList(new GetStsClaimsAction());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return new ArrayList<>();
    }
}
