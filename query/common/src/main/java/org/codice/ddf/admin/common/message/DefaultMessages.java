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

    public static final String MISSING_KEY_VALUE = "MISSING_KEY_VALUE";

    public static final String INVALID_PORT_RANGE = "INVALID_PORT_RANGE";

    public static final String INVALID_HOSTNAME = "INVALID_HOSTNAME";

    public static final String INVALID_CONTEXT_PATH = "INVALID_CONTEXT_PATH";

    public static final String FAILED_PERSIST = "FAILED_PERSIST";

    public static final String UNSUPPORTED_ENUM = "UNSUPPORTED_ENUM";

    public static final String UNKNOWN_ENDPOINT = "UNKNOWN_ENDPOINT";

    public static final String CANNOT_CONNECT = "CANNOT_CONNECT";

    public static final String CERT_ERROR = "CERT_ERROR";

    public static final String UNTRUSTED_CA = "UNTRUSTED_CA";

    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    public static final String FAILED_UPDATE_ERROR = "FAILED_UPDATE_ERROR";

    public static final String FAILED_DELETE_ERROR = "FAILED_DELETE_ERROR";

    public static final ErrorMessage INTERNAL_ERROR_MESSAGE = new ErrorMessage(INTERNAL_ERROR);

    public static ErrorMessage failedPersistError(List<String> path) {
        return new ErrorMessage(FAILED_PERSIST, path);
    }

    public static ErrorMessage unsupportedEnum(List<String> path) {
        return new ErrorMessage(UNSUPPORTED_ENUM, path);
    }

    public static ErrorMessage failedPersistError() {
        return new ErrorMessage(FAILED_PERSIST);
    }

    public static ErrorMessage missingKeyValue(List<String> path) {
        return new ErrorMessage(MISSING_KEY_VALUE, path);
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

    public static ErrorMessage invalidContextPathError(List<String> path) {
        return new ErrorMessage(INVALID_CONTEXT_PATH, path);
    }

    public static ErrorMessage unknownEndpointError(String pathOrigin) {
        return new ErrorMessage(UNKNOWN_ENDPOINT, pathOrigin);
    }

    public static ErrorMessage cannotConnectError(String pathOrigin) {
        return new ErrorMessage(CANNOT_CONNECT, pathOrigin);
    }

    public static ErrorMessage certError(String pathOrigin) {
        return new ErrorMessage(CERT_ERROR, pathOrigin);
    }

    public static ErrorMessage failedUpdateError(List<String> path) {
        return new ErrorMessage(FAILED_UPDATE_ERROR, path);
    }

    public static ErrorMessage failedDeleteError(List<String> path) {
        return new ErrorMessage(FAILED_DELETE_ERROR, path);
    }

    /*
        Warnings
     */

    public static WarningMessage unstrustedCaWarning(String pathOrigin) {
        return new WarningMessage(UNTRUSTED_CA, pathOrigin);
    }
}
