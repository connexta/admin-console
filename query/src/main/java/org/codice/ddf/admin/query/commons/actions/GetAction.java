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
package org.codice.ddf.admin.query.commons.actions;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.query.api.action.Message;
import org.codice.ddf.admin.query.api.fields.Field;

public abstract class GetAction<T extends Field> extends BaseAction<T> {

    public GetAction(String fieldName, String description, T returnType) {
        super(fieldName, description, returnType);
    }

    @Override
    public List<Field> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public List<Message> validate() {
        return new ArrayList<>();
    }
}
