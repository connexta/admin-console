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

/** @param <T> the type of the value contains by this {@code Field} */
public interface Field<T> {

  String getName();

  String getTypeName();

  String getDescription();

  Set<String> getErrorCodes();

  T getValue();

  T getSanitizedValue();

  void setValue(T value);

  boolean isRequired();

  Field<T> isRequired(boolean required);

  List<ErrorMessage> validate();

  List<String> path();

  void pathName(String fieldName);

  void updatePath(List<String> subPath);
}
