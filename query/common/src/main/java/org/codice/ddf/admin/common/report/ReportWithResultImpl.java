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
package org.codice.ddf.admin.common.report;

import java.util.Optional;

import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.api.report.ReportWithResult;

/**
 * A wrapper for an object which also contains argument and result messages.
 *
 * @param <T> type that is wrapped
 */
public class ReportWithResultImpl<T> extends ReportImpl implements ReportWithResult<T> {

    private Optional<T> result;

    public ReportWithResultImpl() {
        super();
        this.result = Optional.empty();
    }

    public ReportWithResultImpl(T result) {
        this();
        this.result = Optional.of(result);
    }

    public void result(T value) {
        this.result = Optional.ofNullable(value);
    }

    public boolean isResultPresent() {
        return result.isPresent();
    }

    public T result() {
        return result.orElseGet(() -> null);
    }

    @Override
    public ReportWithResultImpl<T> addArgumentMessage(Message message) {
        super.addArgumentMessage(message);
        return this;
    }

    @Override
    public ReportWithResultImpl<T> addResultMessage(Message message) {
        super.addResultMessage(message);
        return this;
    }
}
