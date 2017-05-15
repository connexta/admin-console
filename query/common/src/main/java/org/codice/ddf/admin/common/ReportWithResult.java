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

import java.util.List;
import java.util.Optional;

import org.codice.ddf.admin.api.action.Message;

/**
 * A wrapper for an object which also contains argument and result messages.
 *
 * @param <T> type that is wrapped
 */
public class ReportWithResult<T> extends Report {

    private Optional<T> result;

    public ReportWithResult() {
        super();
        this.result = Optional.empty();
    }

    public ReportWithResult(T result) {
        this();
        this.result = Optional.of(result);
    }

    public void result(T value) {
        this.result = Optional.ofNullable(value);
    }

    public boolean isResultPresent() {
        return result.isPresent();
    }

    public boolean isResultNotPresent() {
        return !isResultPresent();
    }

    public T result() {
        return result.orElseGet(() -> null);
    }

    @Override
    public ReportWithResult<T> argumentMessage(Message message) {
        super.argumentMessage(message);
        return this;
    }
}
