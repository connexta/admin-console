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
package org.codice.ddf.admin.api.fields;

import java.util.List;

import org.codice.ddf.admin.api.action.Message;

public interface Field<T> {

    String INDEX_DELIMETER = "__index:";

    String fieldName();

    String fieldTypeName();

    FieldBaseType fieldBaseType();

    String description();

    T getValue();

    void setValue(T value);

    List<Message> validate();

    /**
     * Returns a path that uniquely identifies this {@code Field}.
     *
     * @return a {@code List} of Strings identifying this {@code Field}
     */
    List<String> path();

    /**
     * Adds a sub-path to the list of Strings that describes this {@code Field}'s path.
     *
     * @param subPath unique identifier to add to the path
     */
    void updatePath(String subPath);

    boolean isRequired();

    Field<T> isRequired(boolean required);

    Field<T> matchRequired(Field<T> fieldToMatch);

    enum FieldBaseType {
        STRING, INTEGER, FLOAT, BOOLEAN, LIST, OBJECT, ENUM, UNION
    }
}
