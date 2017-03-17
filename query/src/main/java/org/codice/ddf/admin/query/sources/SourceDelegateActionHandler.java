/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.query.sources;

import java.util.List;

import org.codice.ddf.admin.query.api.action.Action;
import org.codice.ddf.admin.query.commons.actions.BaseActionCreator;
import org.codice.ddf.admin.query.sources.actions.discover.DiscoverSourceByAddressAction;
import org.codice.ddf.admin.query.sources.actions.discover.DiscoverSourceByUrlAction;
import org.codice.ddf.admin.query.sources.actions.discover.GetSourceConfigsAction;
import org.codice.ddf.admin.query.sources.actions.persist.DeleteSource;
import org.codice.ddf.admin.query.sources.actions.persist.SaveCswConfiguration;
import org.codice.ddf.admin.query.sources.actions.persist.SaveOpensearchConfiguration;
import org.codice.ddf.admin.query.sources.actions.persist.SaveWfsConfiguration;

import com.google.common.collect.ImmutableList;

public class SourceDelegateActionHandler extends BaseActionCreator {

    public static final String NAME = "sources";
    public static final String TYPE_NAME = "Sources";
    public static final String DESCRIPTION = "Responsible for delegating tasks and information to all other source handlers.";

    public SourceDelegateActionHandler() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new DiscoverSourceByAddressAction(),
                new DiscoverSourceByUrlAction(),
                new GetSourceConfigsAction());
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of(new SaveCswConfiguration(), new SaveWfsConfiguration(), new SaveOpensearchConfiguration(), new DeleteSource());
    }
}
