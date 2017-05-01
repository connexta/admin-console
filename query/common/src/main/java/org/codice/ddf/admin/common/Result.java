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
 */
package org.codice.ddf.admin.common;

import java.util.Optional;

/**
 * A wrapper for an object which also contains argument and result messages.
 *
 * @param <T> type that is wrapped
 */
// TODO: 4/28/17 phuffer - should we refactor ActionReport to extend this?
public class Result<T> extends Messages {

    private Optional<T> value;

    public Result() {
        super();
        this.value = Optional.empty();
    }

    public Result(T value) {
        this();
        this.value = Optional.of(value);
    }

    public Result<T> value(T value) {
        this.value = Optional.of(value);
        return this;
    }

    public boolean isPresent() {
        return value.isPresent();
    }

    public boolean isNotPresent() {
        return !isPresent();
    }

    public T get() {
        return value.get();
    }
}
