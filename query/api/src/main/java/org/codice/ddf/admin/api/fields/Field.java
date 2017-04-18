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
    String fieldName();

    String fieldTypeName();

    FieldBaseType fieldBaseType();

    String description();

    T getValue();

    void setValue(T value);

    List<Message> validate();

    /**
     * Returns a path to this {@code Field}. A {@code Field}'s path describes the location of the field
     * when nested in {@link ObjectField}'s. For example, if {@code ObjectField} x encapsulates {@code ObjectField} y, which
     * itself encapsulates {@link Field} z, z's path is defined as a list of fields [x, y, z]. If a {@code Field}
     * exists outside an {@link ObjectField}, its path is itself.
     *
     * @return a {@code List} of {@link Field}s describing this {@code Field}'s location.
     */
    List<String> path();

    /**
     * Pushes a {@code Field}'s name to the deque of {@code String}s that describe this {@code Field}'s path.
     *
     * @param fieldName field name to add to path
     */
    void addToPath(String fieldName);

    boolean isRequired();

    Field<T> isRequired(boolean required);

    enum FieldBaseType {
        STRING, INTEGER, FLOAT, BOOLEAN, LIST, OBJECT, ENUM, UNION
    }
}
