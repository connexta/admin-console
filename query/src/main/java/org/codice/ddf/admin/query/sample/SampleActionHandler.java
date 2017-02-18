package org.codice.ddf.admin.query.sample;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;
import org.codice.ddf.admin.query.sample.actions.SampleAction;

public class SampleActionHandler extends DefaultActionHandler {

    public static final String ACTION_HANDLER_ID = "sample";
    public static final String ACTION_HANDLER_DESCRIPTION = "Sample action handler description.";

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
        //new LdapConnectAction(),
        return Arrays.asList( new SampleAction());
    }
}
