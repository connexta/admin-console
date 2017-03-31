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
package org.codice.ddf.admin.utils.conn;

import java.util.List;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.common.actions.BaseActionCreator;
import org.codice.ddf.admin.utils.conn.actions.PingByAddress;
import org.codice.ddf.admin.utils.conn.actions.PingByUrl;

import com.google.common.collect.ImmutableList;

public class ConnectionActionCreator extends BaseActionCreator {

    public static final String NAME = "conn";

    public static final String TYPE_NAME = "Connection";

    public static final String DESCRIPTION = "Provides actions for connecting to urls.";

    public ConnectionActionCreator() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new PingByAddress(), new PingByUrl());
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of();
    }
}