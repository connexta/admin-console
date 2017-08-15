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
package org.codice.ddf.admin.sources.utils;

import java.util.function.Function;

import org.codice.ddf.admin.api.report.Report;

public class SourceTaskHandler<T>
        implements Function<Report<T>, Report<T>> {

    @Override
    public Report<T> apply(Report<T> configResult) {
        if (configResult != null && !configResult.containsErrorMessages()) {
            return configResult;
        }
        return null;
    }
}
