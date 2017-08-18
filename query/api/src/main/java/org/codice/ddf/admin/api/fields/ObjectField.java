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
import java.util.Map;

import org.codice.ddf.admin.api.Field;

/**
 * Encapsulates a {@code List} of {@link Field}s.
 */
public interface ObjectField extends Field<Map<String, Object>> {

    /**
     * Returns all the internal {@link Field}s held by this {@code ObjectField}.
     *
     * @return a {@code List} of this {@code ObjectField}'s {@link Field}s.
     */
    List<Field> getFields();
}
