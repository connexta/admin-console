package org.codice.ddf.admin.query.sources.delegate;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;
import org.codice.ddf.admin.query.sources.delegate.actions.DiscoverSourcesAction;

import com.google.common.collect.ImmutableList;

public class SourceDelegateActionHandler extends DefaultActionHandler {

    public static final String ACTION_ID = "sources";
    public static final String DESCRIPTION = "Responsible for delegating tasks and information to all other source handlers.";

    @Override
    public String getActionHandlerId() {
        return ACTION_ID;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new DiscoverSourcesAction());
    }

    @Override
    public List<Action> getPersistActions() {
        return new ArrayList<>();
    }
}
