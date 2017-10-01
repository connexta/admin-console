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

import java.util.LinkedList;
import java.util.List;
import org.codice.ddf.admin.api.report.ErrorMessage;

public class ErrorMessageImpl implements ErrorMessage {

  private String code;

  private List<Object> path;

  public ErrorMessageImpl(String code) {
    this.code = code;
    path = new LinkedList<>();
  }

  public ErrorMessageImpl(String code, String pathOrigin) {
    this(code);
    path.add(pathOrigin);
  }

  public ErrorMessageImpl(String code, List<Object> path) {
    this(code);
    this.path.addAll(path);
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public List<Object> getPath() {
    return path;
  }

  @Override
  public ErrorMessage setPath(List<Object> path) {
    this.path = path;
    return this;
  }
}
