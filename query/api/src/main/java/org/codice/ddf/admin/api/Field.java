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
import java.util.Set;

/**
 * Defines a container for setting and retrieving values.
 *
 * @param <S> The type of the value used for setting
 * @param <G> The type of the value to be returned
 */
public interface Field<S, G> {

    /**
     * Returns the name of this {@code Field}.
     *
     * @return the name of this field
     */
    String fieldName();

    /**
     * @param fieldName the new name of the {@code Field}
     */
    void pathName(String fieldName);

    /**
     * @return a description describing this {@code Field}
     */
    String description();

    /**
     * Sets the value contained by this {@code Field}
     *
     * @param value the new value
     */
    void setValue(S value);

    /**
     * @return the value of this field
     */
    G getValue();

    /**
     * Returns a path that uniquely identifies this {@code Field}.
     *
     * @return a {@code List} of Strings identifying this {@code Field}
     */
    List<String> path();

    /**
     * Sets the unique sub-path to reach the {@code Field}.
     *
     * @param subPath uniquely identifiable path
     */
    void updatePath(List<String> subPath);

    /**
     * Returns the possible errors that could arise while validating the {@code Field}.
     *
     * @return a {@code Set} of Strings containing the errors.
     */
    Set<String> getErrorCodes();
}
