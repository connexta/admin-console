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

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.FunctionReport;

/**
 * A {@code FunctionField} is capable of processing arguments retrieved by {@link #getArguments()}.
 *
 * @param <T> the return type
 */
public interface FunctionField<T extends DataType> extends Field<Map<String, Object>, FunctionReport<T>> {

    String ARGUMENT = "__argument";

    /**
     * Returns a field definition of the return type from the {@link #getValue()} method.
     *
     * @return the return type of this {@code FunctionField}
     */
    T getReturnType();

    /**
     * @return argument definitions of this {@code FunctionField}
     */
    List<DataType> getArguments();

    FunctionField<T> newInstance();
}
