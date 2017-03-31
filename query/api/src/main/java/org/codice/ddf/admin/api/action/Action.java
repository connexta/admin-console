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
package org.codice.ddf.admin.api.action;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.fields.Field;

/**
 * Intended to be called in the sequence of
 * 1. setArguments
 * 2. validate
 * 3. process
 *
 * @param <T>
 */
public interface Action<T extends Field> {

    String ARGUMENT = "__argument";

    /**
     * Returns the unique name of the Action
     *
     * @return name
     */
    String name();

    /**
     * Returns a description of what the Action is designed to perform and any clarity about it's implementation.
     *
     * @return description
     */
    String description();

    /**
     * Returns a sample field of the expected return type from the process method.
     *
     * @return returnType
     */
    T returnType();

    /**
     * Returns any fields the Action will need for processing.
     *
     * @return args
     */
    List<Field> getArguments();

    /**
     * Sets the arguments used by the Action for processing
     *
     * @param args
     */
    void setArguments(Map<String, Object> args);

    /**
     * Performs an operation.
     *
     * @return
     */
    ActionReport<T> process();

}
