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
import java.util.Set;
import org.codice.ddf.admin.api.report.ErrorMessage;

// TODO: 2/14/18 phuffer - Consider overall statement about nullability or using nonnull/notnull
/**
 * A {@code Field} is a container for a value. It is used by the {@link
 * org.codice.ddf.admin.api.fields.FunctionField} as arguments and return types.
 *
 * @param <T> the type of the value contained by this {@code Field}
 */
public interface Field<T> {

  /**
   * The name of this field.
   *
   * @return the name, cannot be null or empty string
   */
  String getFieldName();

  /**
   * The type of this field. Any field with the same type must have the same description, error
   * codes, and value type <T>.
   *
   * @return the field type, cannot be null or empty string
   */
  String getFieldType();

  /**
   * The description of this field. A good description describes the requirements for this field and
   * its purpose.
   *
   * @return the description, cannot be null or empty
   */
  String getDescription();

  /**
   * Human readable codes describing different errors that can be returned from validation of this
   * field. Good error codes give an accurate description of what went wrong. An example of a good
   * error code is "INVALID_PORT_RANGE".
   *
   * @return set of error codes, or empty if there are none
   */
  Set<String> getErrorCodes();

  /**
   * Returns the original value contained in this field.
   *
   * @return the value, can be null
   */
  T getValue();

  /**
   * Returns a sanitized value of this field. For example, for a field that represents a password,
   * this method would be called to "mask" the password.
   *
   * <p>This method should delegate to {@link #getValue()} if this field's value does not
   * need to be sanitized.
   *
   * @return the sanitized value, cannot be null
   */
  T getSanitizedValue();

  /**
   * Sets the value inside this field.
   *
   * @param value the value of type <T>
   */
  void setValue(T value);

  // TODO: 2/12/18 phuffer - Consider breaking this out into a separate ArgumentField
  /**
   * When this field acts an argument to a {@link org.codice.ddf.admin.api.fields.FunctionField},
   * the {@code isRequired} property determines whether or not the function field requires this
   * field argument.
   *
   * @return whether or not this field argument is required
   */
  boolean isRequired();

  // TODO: 2/12/18 phuffer - Consider removing builder pattern from interface and moving to
  // ArgumentField
  /**
   * Sets the requirement of this field.
   *
   * @param required whether or not this field argument is required
   * @return this field
   */
  // TODO: 2/14/18 phuffer - Consider renaming to setRequired
  Field<T> isRequired(boolean required);

  // TODO: 2/12/18 phuffer - Consider returning a Set<ErrorMessage>
  /**
   * Validates this field's value.
   *
   * @return empty list if there are errors validating this field's value, otherwise empty.
   */
  List<ErrorMessage> validate();

  // TODO: 2/12/18 phuffer - Consider breaking this out into a separate ArgumentField
  /**
   * The unique ordered path that identifies the location of this field. For example, a path may
   * contain a list of other field names and indices.
   *
   * @return the unique path of this field, or a singleton list with this field's name
   */
  List<Object> getPath();

  // TODO: 2/14/18 phuffer - To ArgumentField as well
  /**
   * Sets the path of this field
   *
   * @param path the path
   */
  void setPath(List<Object> path);
}
