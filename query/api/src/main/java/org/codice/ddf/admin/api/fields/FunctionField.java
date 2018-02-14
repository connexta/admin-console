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

// TODO: 2/14/18 phuffer - Consider changing this name (Executor?)
// TODO: 2/14/18 phuffer - Consider different package name. (endpoint.model?)
/**
 * A {@code FunctionField} is capable of processing arguments retrieved by {@link #getArguments()}.
 *
 * @param <T> the return type
 */
public interface FunctionField<T extends Field> {

  /**
   * Returns the unique name of this function.
   *
   * @return the name, cannot be null
   */
  String getFunctionName();

  /**
   * A description of this function. A good function describes what it does, as well as how it is
   * used.
   *
   * @return the description, cannot be null
   */
  String getDescription();

  // TODO: 2/14/18 phuffer - Consider returning a set of ErrorMessage
  /**
   * Returns a set of error codes that can be returned by this function.
   *
   * @return a set with error codes, or empty set if none
   */
  Set<String> getErrorCodes();

  /**
   * The arguments of this function. A function can have 0..N arguments. Arguments must have unique
   * names.
   *
   * @return this functions arguments, or empty list if none
   */
  List<Field> getArguments();

  /**
   * Gets a blank prototype instance of the return type of this function. This instance should not
   * be used for anything other than introspection.
   *
   * @return the return type of this field, cannot be null
   */
  T getReturnType();

  // TODO: 2/14/18 phuffer - Consider breaking functionPath into setPath method
  /**
   * Executes this function against its arguments.
   *
   * @param args a map containing argument names to argument values
   * @param functionPath
   * @return the report of this function's result
   */
  FunctionReport<T> execute(Map<String, Object> args, List<Object> functionPath);

  // TODO: 2/14/18 phuffer - Consider changing to create and adding functionPath argument. Can we
  // know the path at object creation time?
  /** @return */
  FunctionField<T> newInstance();

  // TODO: 2/14/18 phuffer - Review this
  /**
   * The unique ordered path that identifies the location of this function. For example, a path may
   * contain a list of other field names and indices.
   *
   * @return the unique path of this function, or a singleton list with this function name
   */
  List<Object> getPath();
}
