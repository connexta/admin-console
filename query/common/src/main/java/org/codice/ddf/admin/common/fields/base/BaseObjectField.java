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
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.report.ErrorMessage;

public abstract class BaseObjectField extends BaseField<Map<String, Object>>
    implements ObjectField {

  public BaseObjectField(String fieldName, String fieldTypeName, String description) {
    super(fieldName, fieldTypeName, description);
  }

  @Override
  public Map<String, Object> getValue() {
    Map<String, Object> values = new HashMap<>();

    for (Field field : getFields()) {
      values.put(field.getFieldName(), field.getValue());
    }

    return values;
  }

  @Override
  public Map<String, Object> getSanitizedValue() {
    Map<String, Object> values = new HashMap<>();

    for (Field field : getFields()) {
      values.put(field.getFieldName(), field.getSanitizedValue());
    }

    return values;
  }

  @Override
  public void setValue(Map<String, Object> values) {
    if (values == null || values.isEmpty()) {
      return;
    }

    getFields()
        .stream()
        .filter(field -> values.containsKey(field.getFieldName()))
        .forEach(field -> field.setValue(values.get(field.getFieldName())));
  }

  @Override
  public List<ErrorMessage> validate() {
    List<ErrorMessage> validationErrors = super.validate();

    if (!validationErrors.isEmpty()) {
      return validationErrors;
    }

    validationErrors.addAll(
        getFields()
            .stream()
            .map(field -> (List<ErrorMessage>) field.validate())
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
    return validationErrors;
  }

  @Override
  public void setPath(List<Object> path) {
    super.setPath(path);
    getFields()
        .stream()
        .filter(Objects::nonNull)
        .forEach(child -> child.setPath(createInnerFieldPath(getPath(), child.getFieldName())));
  }

  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .addAll(
            getFields()
                .stream()
                .map(field -> field.getErrorCodes())
                .flatMap(Collection<String>::stream)
                .collect(Collectors.toList()))
        .build();
  }

  private List<Object> createInnerFieldPath(List<Object> path, String childName) {
    return new ImmutableList.Builder<>().addAll(path).add(childName).build();
  }
}
