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

    public static ErrorMessage noRootContextError(String pathOrigin) {
        return new ErrorMessage(NO_ROOT_CONTEXT, pathOrigin);
    }

    public static ErrorMessage failedPersistError(String pathOrigin) {
        return new ErrorMessage(FAILED_PERSIST, pathOrigin);
    }

    public static ErrorMessage invalidFieldError(String pathOrigin) {
        return new ErrorMessage(INVALID_FIELD, pathOrigin);
    }

    public static ErrorMessage invalidContextPathError(String pathOrigin) {
        return new ErrorMessage(INVALID_CONTEXT_PATH, pathOrigin);
    }

    public static ErrorMessage invalidPortRangeError(String pathOrigin) {
        return new ErrorMessage(INVALID_PORT_RANGE, pathOrigin);
    }

    public static ErrorMessage missingRequiredFieldError(String pathOrigin) {
        return new ErrorMessage(MISSING_REQUIRED_FIELD, pathOrigin);
    }

    public static ErrorMessage emptyFieldError(String pathOrigin) {
        return new ErrorMessage(EMPTY_FIELD, pathOrigin);
    }

    public static ErrorMessage invalidHostnameError(String pathOrigin) {
        return new ErrorMessage(INVALID_HOSTNAME, pathOrigin);
    }

    public static ErrorMessage invalidClaimType(String pathOrigin) {
        return new ErrorMessage(INVALID_CLAIM_TYPE, pathOrigin);
    }

}
