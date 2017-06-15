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
package org.codice.ddf.admin.common.report.message;

import java.util.List;

import org.codice.ddf.admin.api.report.ErrorMessage;

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

    public static final String INVALID_URL_ERROR = "INVALID_URL";

    public static final String NO_EXISTING_CONFIG = "NO_EXISTING_CONFIG";

    public static final String INVALID_URI_ERROR = "INVALID_URI";

    public static final String UNAUTHORIZED = "UNAUTHORIZED";

    public static final String FAILED_TEST_SETUP = "FAILED_TEST_SETUP";

    public static final String DUPLICATE_MAP_KEY = "DUPLICATE_MAP_KEY";

    public static ErrorMessage failedTestSetup() {
        return new ErrorMessageImpl(FAILED_TEST_SETUP);
    }

    public static ErrorMessage failedPersistError() {
        return new ErrorMessageImpl(FAILED_PERSIST);
    }

    public static ErrorMessage cannotConnectError() {
        return new ErrorMessageImpl(CANNOT_CONNECT);
    }

    public static ErrorMessage noExistingConfigError() {
        return new ErrorMessageImpl(NO_EXISTING_CONFIG);
    }

    public static ErrorMessage failedPersistError(List<String> path) {
        return new ErrorMessageImpl(FAILED_PERSIST, path);
    }

    public static ErrorMessage unsupportedEnum(List<String> path) {
        return new ErrorMessageImpl(UNSUPPORTED_ENUM, path);
    }

    public static ErrorMessage missingKeyValue(List<String> path) {
        return new ErrorMessageImpl(MISSING_KEY_VALUE, path);
    }

    public static ErrorMessage invalidPortRangeError(List<String> path) {
        return new ErrorMessageImpl(INVALID_PORT_RANGE, path);
    }

    public static ErrorMessage missingRequiredFieldError(List<String> path) {
        return new ErrorMessageImpl(MISSING_REQUIRED_FIELD, path);
    }

    public static ErrorMessage emptyFieldError(List<String> path) {
        return new ErrorMessageImpl(EMPTY_FIELD, path);
    }

    public static ErrorMessage invalidHostnameError(List<String> path) {
        return new ErrorMessageImpl(INVALID_HOSTNAME, path);
    }

    public static ErrorMessage invalidContextPathError(List<String> path) {
        return new ErrorMessageImpl(INVALID_CONTEXT_PATH, path);
    }

    public static ErrorMessage unknownEndpointError(List<String> path) {
        return new ErrorMessageImpl(UNKNOWN_ENDPOINT, path);
    }

    public static ErrorMessage cannotConnectError(List<String> path) {
        return new ErrorMessageImpl(CANNOT_CONNECT, path);
    }

    public static ErrorMessage invalidUrlError(List<String> path) {
        return new ErrorMessageImpl(INVALID_URL_ERROR, path);
    }

    public static ErrorMessage invalidUriError(List<String> path) {
        return new ErrorMessageImpl(INVALID_URI_ERROR, path);
    }

    public static ErrorMessage noExistingConfigError(List<String> path) {
        return new ErrorMessageImpl(NO_EXISTING_CONFIG, path);
    }

    public static ErrorMessage unauthorizedError(List<String> path) {
        return new ErrorMessageImpl(UNAUTHORIZED, path);
    }

    public static ErrorMessage duplicateMapKeyError(List<String> path) {
        return new ErrorMessageImpl(DUPLICATE_MAP_KEY, path);
    }
}
