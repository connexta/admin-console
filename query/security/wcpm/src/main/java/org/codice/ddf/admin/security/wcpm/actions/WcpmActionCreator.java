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
package org.codice.ddf.admin.security.wcpm.actions;

import java.util.List;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.common.actions.BaseActionCreator;
import org.codice.ddf.admin.security.wcpm.actions.discover.GetAuthTypes;
import org.codice.ddf.admin.security.wcpm.actions.discover.GetContextPolicies;
import org.codice.ddf.admin.security.wcpm.actions.discover.GetRealms;
import org.codice.ddf.admin.security.wcpm.actions.discover.GetWhiteListContexts;
import org.codice.ddf.admin.security.wcpm.actions.persist.SaveContextPolices;
import org.codice.ddf.admin.security.wcpm.actions.persist.SaveWhitelistedContexts;

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
        return ImmutableList.of(new GetAuthTypes(),
                new GetRealms(),
                new GetWhiteListContexts(),
                new GetContextPolicies());
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of(new SaveContextPolices(), new SaveWhitelistedContexts());
    }
}
