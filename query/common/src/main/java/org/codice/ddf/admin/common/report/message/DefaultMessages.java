/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
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

  public static final String INVALID_URL = "INVALID_URL";

  public static final String NO_EXISTING_CONFIG = "NO_EXISTING_CONFIG";

  public static final String INVALID_URI = "INVALID_URI";

  public static final String UNAUTHORIZED = "UNAUTHORIZED";

  public static final String FAILED_TEST_SETUP = "FAILED_TEST_SETUP";

  public static final String DUPLICATE_MAP_KEY = "DUPLICATE_MAP_KEY";

  public static final String SIMILAR_SERVICE_EXISTS = "SIMILAR_SERVICE_EXISTS";

  public static final String INVALID_PATH_TRAILING_SLASH = "INVALID_PATH_TRAILING_SLASH";

  private DefaultMessages() {}

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

  public static ErrorMessage unknownEndpointError() {
    return new ErrorMessageImpl(UNKNOWN_ENDPOINT);
  }

  public static ErrorMessage failedPersistError(List<Object> path) {
    return new ErrorMessageImpl(FAILED_PERSIST, path);
  }

  public static ErrorMessage unsupportedEnum(List<Object> path) {
    return new ErrorMessageImpl(UNSUPPORTED_ENUM, path);
  }

  public static ErrorMessage missingKeyValue(List<Object> path) {
    return new ErrorMessageImpl(MISSING_KEY_VALUE, path);
  }

  public static ErrorMessage invalidPortRangeError(List<Object> path) {
    return new ErrorMessageImpl(INVALID_PORT_RANGE, path);
  }

  public static ErrorMessage missingRequiredFieldError(List<Object> path) {
    return new ErrorMessageImpl(MISSING_REQUIRED_FIELD, path);
  }

  public static ErrorMessage emptyFieldError(List<Object> path) {
    return new ErrorMessageImpl(EMPTY_FIELD, path);
  }

  public static ErrorMessage invalidHostnameError(List<Object> path) {
    return new ErrorMessageImpl(INVALID_HOSTNAME, path);
  }

  public static ErrorMessage invalidContextPathError(List<Object> path) {
    return new ErrorMessageImpl(INVALID_CONTEXT_PATH, path);
  }

  public static ErrorMessage unknownEndpointError(List<Object> path) {
    return new ErrorMessageImpl(UNKNOWN_ENDPOINT, path);
  }

  public static ErrorMessage cannotConnectError(List<Object> path) {
    return new ErrorMessageImpl(CANNOT_CONNECT, path);
  }

  public static ErrorMessage invalidUrlError(List<Object> path) {
    return new ErrorMessageImpl(INVALID_URL, path);
  }

  public static ErrorMessage invalidUriError(List<Object> path) {
    return new ErrorMessageImpl(INVALID_URI, path);
  }

  public static ErrorMessage noExistingConfigError(List<Object> path) {
    return new ErrorMessageImpl(NO_EXISTING_CONFIG, path);
  }

  public static ErrorMessage unauthorizedError(List<Object> path) {
    return new ErrorMessageImpl(UNAUTHORIZED, path);
  }

  public static ErrorMessage duplicateMapKeyError(List<Object> path) {
    return new ErrorMessageImpl(DUPLICATE_MAP_KEY, path);
  }

  public static ErrorMessage similarServiceExists(List<Object> path) {
    return new ErrorMessageImpl(SIMILAR_SERVICE_EXISTS, path);
  }

  public static ErrorMessage invalidPathTrailingSlash(List<Object> path) {
    return new ErrorMessageImpl(INVALID_PATH_TRAILING_SLASH, path);
  }
}
