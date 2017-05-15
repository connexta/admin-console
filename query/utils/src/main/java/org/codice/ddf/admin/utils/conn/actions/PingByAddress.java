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
package org.codice.ddf.admin.utils.conn.actions;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.TestAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.HostField;

import com.google.common.collect.ImmutableList;

public class PingByAddress extends TestAction {
    public static final String NAME = "pingByAddress";

    public static final String DESCRIPTION = "Attempts to reach the given URL or hostname and port.";

    private AddressField address;

    public PingByAddress() {
        super(NAME, DESCRIPTION);
        address = new AddressField();
        address.isRequired(true);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(address);
    }

    @Override
    public BooleanField performAction() {
        return new BooleanField(true);
    }
}
