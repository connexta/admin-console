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
package org.codice.ddf.admin.sources.commons;

import java.util.List;

import org.codice.ddf.admin.common.message.ErrorMessage;

public class SourceMessages {

    public static final String DUPLICATE_SOURCE_NAME = "DUPLICATE_SOURCE_NAME";

    public static final String UNKNOWN_SOURCE_TYPE = "UNKNOWN_SOURCE_TYPE";

    public static ErrorMessage duplicateSourceNameError(List<String> path) {
        return new ErrorMessage(DUPLICATE_SOURCE_NAME, path);
    }

    public static ErrorMessage unknownSourceTypeError(List<String> path) {
        return new ErrorMessage(UNKNOWN_SOURCE_TYPE, path);
    }
}
