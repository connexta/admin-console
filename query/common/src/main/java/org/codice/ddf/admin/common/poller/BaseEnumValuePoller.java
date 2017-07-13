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
package org.codice.ddf.admin.common.poller;

import org.codice.ddf.admin.api.poller.EnumValuePoller;
import org.codice.ddf.admin.common.services.ServiceCommons;

public abstract class BaseEnumValuePoller<V, T> implements EnumValuePoller<V, T> {

    private static final String BINDING_ENUM_VALUE = "Binding enum value for %s";

    private static final String UNBINDING_ENUM_VALUE = "Unbinding enum value for %s";

    @Override
    public void bindValue(V value) {
        ServiceCommons.updateGraphQLSchema(getClass(), String.format(BINDING_ENUM_VALUE, getClass()));
    }

    @Override
    public void unbindValue(V value) {
        ServiceCommons.updateGraphQLSchema(getClass(), String.format(UNBINDING_ENUM_VALUE, getClass()));
    }
}
