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
package org.codice.ddf.admin.api;

import java.util.List;

import org.codice.ddf.admin.api.report.Message;

/**
 *
 *
 * @param <T> the type of the value contains by this {@code DataType}
 */
public interface DataType<T> extends Field<T, T> {

    String fieldTypeName();

    boolean isRequired();

    List<Message> validate();

    DataType<T> isRequired(boolean required);

    DataType<T> matchRequired(DataType<T> fieldToMatch);

    // TODO: tbatie - 5/13/17 - Remove this baseDataType, replace with interface checks. These should be moved to a scalar field or something
    FieldBaseType baseDataType();

    enum FieldBaseType {
        STRING, INTEGER, FLOAT, BOOLEAN, LIST, OBJECT, ENUM, UNION
    }
}
