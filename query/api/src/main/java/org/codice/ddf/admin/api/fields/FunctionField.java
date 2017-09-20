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

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.FunctionReport;

/**
 * A {@code FunctionField} is capable of processing arguments retrieved by {@link #getArguments()}.
 *
 * @param <T> the return type
 */
public interface FunctionField<T extends Field> {

  String ARGUMENT = "__argument";

  String getName();

  String getDescription();

  Set<String> getErrorCodes();

  List<Field> getArguments();

  T getReturnType();

  void setArguments(Map<String, Object> value);

  FunctionReport<T> execute();

  FunctionField<T> newInstance();

  List<String> path();

  void pathName(String functionFieldName);

  void updatePath(List<String> subPath);
}
