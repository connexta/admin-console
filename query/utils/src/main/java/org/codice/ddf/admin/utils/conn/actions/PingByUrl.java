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
import org.codice.ddf.admin.common.fields.common.UrlField;

import com.google.common.collect.ImmutableList;

public class PingByUrl extends TestAction {

    public static final String NAME = "pingByUrl";

    public static final String DESCRIPTION = "Attempts to reach the given url.";

    private UrlField url;

    public PingByUrl() {
        super(NAME, DESCRIPTION);
        url = new UrlField();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(url);
    }

    @Override
    public BooleanField performAction() {
        BooleanField testPassed = new BooleanField();
        testPassed.setValue(true);
        return testPassed;
    }
}