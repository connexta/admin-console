package org.codice.ddf.admin.query.sts;

import java.util.List;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;

public class StsActionHandler extends DefaultActionHandler{

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
    public List<Action> getSupportedActions() {
        return null;
    }
}
