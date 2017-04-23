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
package org.codice.ddf.admin.common.message;

import java.util.List;

public class DefaultMessages {

    public static final String MISSING_REQUIRED_FIELD = "MISSING_REQUIRED_FIELD";

    public static final String EMPTY_FIELD = "EMPTY_FIELD";

    public static final String INVALID_FIELD = "INVALID_FIELD";

    public static final String INVALID_PORT_RANGE = "INVALID_PORT_RANGE";

    public static final String INVALID_HOSTNAME = "INVALID_HOSTNAME";

    public static final String INVALID_CONTEXT_PATH = "INVALID_CONTEXT_PATH";

    public static final String NO_ROOT_CONTEXT = "NO_ROOT_CONTEXT";

    public static final String FAILED_PERSIST = "FAILED_PERSIST";

    public static final String INVALID_CLAIM_TYPE = "INVALID_CLAIM_TYPE";

    public static final String UNSUPPORTED_ENUM = "UNSUPPORTED_ENUM";

    public static ErrorMessage unsupportedEnum(List<String> path) {
        return new ErrorMessage(UNSUPPORTED_ENUM, path);
    }

    // TODO: tbatie - 4/22/17 - Move the security related messages to the security commons
    public static ErrorMessage noRootContextError(List<String> path) {
        return new ErrorMessage(NO_ROOT_CONTEXT, path);
    }

    public static ErrorMessage failedPersistError() {
        return new ErrorMessage(FAILED_PERSIST);
    }

    public static ErrorMessage invalidFieldError(List<String> path) {
        return new ErrorMessage(INVALID_FIELD, path);
    }

    public static ErrorMessage invalidContextPathError(List<String> path) {
        return new ErrorMessage(INVALID_CONTEXT_PATH, path);
    }

    public static ErrorMessage invalidPortRangeError(List<String> path) {
        return new ErrorMessage(INVALID_PORT_RANGE, path);
    }

    public static ErrorMessage missingRequiredFieldError(List<String> path) {
        return new ErrorMessage(MISSING_REQUIRED_FIELD, path);
    }

    public static ErrorMessage emptyFieldError(List<String> path) {
        return new ErrorMessage(EMPTY_FIELD, path);
    }

    public static ErrorMessage invalidHostnameError(List<String> path) {
        return new ErrorMessage(INVALID_HOSTNAME, path);
    }

    public static ErrorMessage invalidClaimType(List<String> path) {
        return new ErrorMessage(INVALID_CLAIM_TYPE, path);
    }

}
