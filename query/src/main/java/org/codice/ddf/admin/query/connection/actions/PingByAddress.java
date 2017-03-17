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
package org.codice.ddf.admin.query.connection.actions;

import static org.codice.ddf.admin.query.commons.sample.SampleFields.SAMPLE_REPORT;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.AddressField;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;

import com.google.common.collect.ImmutableList;

public class PingByAddress extends TestAction {
    public static final String NAME = "pingByAddress";
    public static final String DESCRIPTION = "Attempts to reach the given address";

    private AddressField address;

    public PingByAddress() {
        super(NAME, DESCRIPTION);
        address = new AddressField();
    }

    @Override
    public ReportField process() {
        return SAMPLE_REPORT;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(address);
    }
}
