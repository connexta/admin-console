package org.codice.ddf.admin.query.sources;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.BaseActionHandler;
import org.codice.ddf.admin.query.sources.actions.persist.DeleteSource;
import org.codice.ddf.admin.query.sources.actions.persist.SaveCswConfiguration;
import org.codice.ddf.admin.query.sources.actions.discover.DiscoverSourceByAddressAction;
import org.codice.ddf.admin.query.sources.actions.discover.DiscoverSourceByUrlAction;
import org.codice.ddf.admin.query.sources.actions.discover.GetSourceConfigsAction;
import org.codice.ddf.admin.query.sources.actions.persist.SaveOpensearchConfiguration;
import org.codice.ddf.admin.query.sources.actions.persist.SaveWfsConfiguration;

import com.google.common.collect.ImmutableList;

public class SourceDelegateActionHandler extends BaseActionHandler {

    public static final String FIELD_NAME = "sources";
    public static final String FIELD_TYPE_NAME = "Sources";
    public static final String DESCRIPTION = "Responsible for delegating tasks and information to all other source handlers.";

    public SourceDelegateActionHandler() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return ImmutableList.of(new DiscoverSourceByAddressAction(),
                new DiscoverSourceByUrlAction(),
                new GetSourceConfigsAction());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return ImmutableList.of(new SaveCswConfiguration(), new SaveWfsConfiguration(), new SaveOpensearchConfiguration(), new DeleteSource());
    }
}
