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
package org.codice.ddf.admin.api.report;

import java.util.List;

// TODO: 2/20/18 phuffer - Extract getPath() + setPath() methods into separate interface
// (Traversable?) and extend it here
/** Defines a contract for errors that appear in {@link Report}s. */
public interface ErrorMessage {

  /**
   * A human-readable code defining this error. A code should relay enough information of the
   * problem. For example, "INVALID_PORT_RANGE".
   *
   * @return the human-readable code for this error message
   */
  String getCode();

  /**
   * The unique ordered path that identifies the location of the {@link
   * org.codice.ddf.admin.api.Field} or {@link org.codice.ddf.admin.api.fields.FunctionField} in
   * error. For example, a path may contain a list of other field or function names and indices.
   *
   * @return the unique path of identifying the field or function in error, cannot be empty
   */
  List<Object> getPath();

  // TODO: 2/20/18 phuffer - Return void instead
  /**
   * Sets the path to the field or function for which this error message applies
   *
   * @param path the path of the field or function
   */
  ErrorMessage setPath(List<Object> path);
}
