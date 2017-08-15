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

import org.codice.ddf.admin.api.report.ErrorMessage;

/**
 *
 *
 * @param <T> the type of the value contains by this {@code Field}
 */
public interface Field<T> {

    /**
     * Returns the name of this {@code Field}.
     *
     * @return the name of this data type
     */
    String getName();

    String getTypeName();

    /**
     * @return a description describing this {@code Field}
     */
    String getDescription();

    /**
     * Returns the possible errors that could arise while validating the {@code Field}.
     *
     * @return a {@code Set} of Strings containing the errors.
     */
    Set<String> getErrorCodes();

    /**
     * @return the value of this {@code Field}
     */
    T getValue();

    /**
     * Sets the value contained by this {@code Field}
     *
     * @param value the new value
     */
    void setValue(T value);

    boolean isRequired();

    Field<T> isRequired(boolean required);

    List<ErrorMessage> validate();

    /**
     * Returns a path that uniquely identifies this {@code Field}.
     *
     * @return a {@code List} of Strings identifying this {@code Field}
     */
    List<String> path();

    /**
     * @param fieldName the new name of the {@code Field}
     */
    void pathName(String fieldName);

    /**
     * Sets the unique sub-path to reach the {@code Field}.
     *
     * @param subPath uniquely identifiable path
     */
    void updatePath(List<String> subPath);
}

