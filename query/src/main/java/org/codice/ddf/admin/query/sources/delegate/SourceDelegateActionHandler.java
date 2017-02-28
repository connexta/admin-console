package org.codice.ddf.admin.query.sources.delegate;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;
import org.codice.ddf.admin.query.sources.delegate.actions.DiscoverSourcesAction;

import com.google.common.collect.ImmutableList;

public class SourceDelegateActionHandler extends DefaultActionHandler {

    public static final String FIELD_NAME = "sources";
    public static final String DESCRIPTION = "Responsible for delegating tasks and information to all other source handlers.";

    public SourceDelegateActionHandler() {
        super(FIELD_NAME, DESCRIPTION);
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return ImmutableList.of(new DiscoverSourcesAction());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return new ArrayList<>();
    }
}
