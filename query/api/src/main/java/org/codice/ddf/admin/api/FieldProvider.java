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
package org.codice.ddf.admin.api;

import java.util.List;

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ObjectField;

// TODO: 2/14/18 phuffer - Rename this from FieldProvider
// TODO: 2/14/18 phuffer - Does this really need to extend object field? Is this really a Field?
/** Provides a list of read-only and mutative functions. */
public interface FieldProvider extends ObjectField {

  // TODO: 2/14/18 phuffer - Consider renaming to getQueryFunctions() (or something else)
  /**
   * Returns a list of functions registered in this provider that perform read-only operations. For
   * example: retrieving system configurations, testing user input, querying external systems, etc.
   *
   * @return list of non-destructive functions, cannot be null
   */
  List<FunctionField> getDiscoveryFunctions();

  /**
   * Returns a list of functions registered in this provider that perform mutative operations
   * (creates, updates, deletes, etc).
   *
   * @return list of mutative functions, cannot be null
   */
  List<FunctionField> getMutationFunctions();
}
