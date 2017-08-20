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

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.utils.conn.discover.PingByAddress;

import com.google.common.collect.ImmutableList;

public class ConnectionFieldProvider extends BaseFieldProvider {

    public static final String NAME = "conn";

    public static final String TYPE_NAME = "Connection";

    public static final String DESCRIPTION = "Provides methods for connecting to urls.";

    private PingByAddress pingByAddress;

    public ConnectionFieldProvider() {
        super(NAME, TYPE_NAME, DESCRIPTION);
        pingByAddress = new PingByAddress();
        updateInnerFieldPaths();
    }

    @Override
    public List<FunctionField> getDiscoveryFunctions() {
        return ImmutableList.of(pingByAddress);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of();
    }
}