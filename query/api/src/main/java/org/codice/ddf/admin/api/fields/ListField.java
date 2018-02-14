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
package org.codice.ddf.admin.api.fields;

import java.util.Collection;
import java.util.List;
import org.codice.ddf.admin.api.Field;

// TODO: 2/12/18 phuffer - Investigate getting field's generic type in here
/**
 * An ordered list of fields
 *
 * @param <T> field type contained by this list
 */
public interface ListField<T extends Field> extends Field<List> {

  /**
   * Creates an entry for this list.
   *
   * @return the newly created entry, cannot be null
   */
  T createListEntry();

  /**
   * Returns the fields contained in this {@code ListField} in a simple collection.
   *
   * @return this list
   */
  List<T> getList();

  // TODO: 2/14/18 phuffer - Investigate if anyone is using builder pattern
  /**
   * Adds a field to this list
   *
   * @param field the field to add
   * @return this list
   */
  ListField<T> add(T field);

  /**
   * Adds a collection of fields to this list
   *
   * @param fields the collection of fields
   * @return this list
   */
  ListField<T> addAll(Collection<T> fields);
}
