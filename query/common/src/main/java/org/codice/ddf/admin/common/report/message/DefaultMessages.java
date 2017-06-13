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

    public static final String FAILED_UPDATE_ERROR = "FAILED_UPDATE";

    public static final String FAILED_DELETE_ERROR = "FAILED_DELETE";

    public static final String INVALID_URL_ERROR = "INVALID_URL";

    public static final String NO_EXISTING_CONFIG = "NO_EXISTING_CONFIG";

    public static final String INVALID_URI_ERROR = "INVALID_URI";

    public static final String UNAUTHORIZED = "UNAUTHORIZED";

    public static final String DUPLICATE_MAP_KEY = "DUPLICATE_MAP_KEY";

    public static ErrorMessageImpl failedPersistError() {
        return new ErrorMessageImpl(FAILED_PERSIST);
    }

    public static ErrorMessageImpl failedDeleteError() {
        return new ErrorMessageImpl(FAILED_DELETE_ERROR);
    }

    public static ErrorMessageImpl failedUpdateError() {
        return new ErrorMessageImpl(FAILED_UPDATE_ERROR);
    }

    public static ErrorMessageImpl cannotConnectError() {
        return new ErrorMessageImpl(CANNOT_CONNECT);
    }

    public static ErrorMessageImpl noExistingConfigError() {
        return new ErrorMessageImpl(NO_EXISTING_CONFIG);
    }

    public static ErrorMessageImpl failedPersistError(List<String> path) {
        return new ErrorMessageImpl(FAILED_PERSIST, path);
    }

    public static ErrorMessageImpl unsupportedEnum(List<String> path) {
        return new ErrorMessageImpl(UNSUPPORTED_ENUM, path);
    }

    public static ErrorMessageImpl missingKeyValue(List<String> path) {
        return new ErrorMessageImpl(MISSING_KEY_VALUE, path);
    }

    public static ErrorMessageImpl invalidPortRangeError(List<String> path) {
        return new ErrorMessageImpl(INVALID_PORT_RANGE, path);
    }

    public static ErrorMessageImpl missingRequiredFieldError(List<String> path) {
        return new ErrorMessageImpl(MISSING_REQUIRED_FIELD, path);
    }

    public static ErrorMessageImpl emptyFieldError(List<String> path) {
        return new ErrorMessageImpl(EMPTY_FIELD, path);
    }

    public static ErrorMessageImpl invalidHostnameError(List<String> path) {
        return new ErrorMessageImpl(INVALID_HOSTNAME, path);
    }

    public static ErrorMessageImpl invalidContextPathError(List<String> path) {
        return new ErrorMessageImpl(INVALID_CONTEXT_PATH, path);
    }

    public static ErrorMessageImpl unknownEndpointError(List<String> path) {
        return new ErrorMessageImpl(UNKNOWN_ENDPOINT, path);
    }

    public static ErrorMessageImpl cannotConnectError(List<String> path) {
        return new ErrorMessageImpl(CANNOT_CONNECT, path);
    }

    public static ErrorMessageImpl failedUpdateError(List<String> path) {
        return new ErrorMessageImpl(FAILED_UPDATE_ERROR, path);
    }

    public static ErrorMessageImpl failedDeleteError(List<String> path) {
        return new ErrorMessageImpl(FAILED_DELETE_ERROR, path);
    }

    public static ErrorMessageImpl invalidUrlError(List<String> path) {
        return new ErrorMessageImpl(INVALID_URL_ERROR, path);
    }

    public static ErrorMessageImpl invalidUriError(List<String> path) {
        return new ErrorMessageImpl(INVALID_URI_ERROR, path);
    }

    public static ErrorMessageImpl noExistingConfigError(List<String> path) {
        return new ErrorMessageImpl(NO_EXISTING_CONFIG, path);
    }

    public static ErrorMessageImpl unauthorizedError(List<String> path) {
        return new ErrorMessageImpl(UNAUTHORIZED, path);
    }

    public static ErrorMessageImpl duplicateMapKeyError(List<String> path) {
        return new ErrorMessageImpl(DUPLICATE_MAP_KEY, path);
    }
}
