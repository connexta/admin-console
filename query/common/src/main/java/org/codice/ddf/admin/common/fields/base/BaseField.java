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
package org.codice.ddf.admin.common.fields.base;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.codice.ddf.admin.api.Field;

public abstract class BaseField<S, G> implements Field<S, G> {

  private String fieldName;

  private String description;

  private List<String> subpath;

  private String pathName;

  public BaseField(String fieldName, String description) {
    this.fieldName = fieldName;
    this.description = description;
    subpath = new ArrayList<>();
    pathName = fieldName;
  }

  @Override
  public String fieldName() {
    return fieldName;
  }

  @Override
  public void pathName(String pathName) {
    this.pathName = pathName;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public List<String> path() {
    return new ImmutableList.Builder().addAll(subpath).add(pathName).build();
  }

  @Override
  public void updatePath(List<String> subPath) {
    subpath.clear();
    subpath.addAll(subPath);
  }
}
