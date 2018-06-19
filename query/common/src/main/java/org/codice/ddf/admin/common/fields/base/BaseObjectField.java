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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class BaseObjectField extends BaseField<Map<String, Object>>
    implements ObjectField {

  /**
   * Constructor.
   *
   * @param fieldName name of this {@link Field}
   * @param fieldTypeName name of the type of this {@link Field}
   * @param description description of this {@link Field}
   */
  public BaseObjectField(String fieldName, String fieldTypeName, String description) {
    super(fieldName, fieldTypeName, description);
  }

  /** @return a map of this object's inner {@link Field}s' names and their values */
  @Override
  public Map<String, Object> getValue() {
    Map<String, Object> values = new HashMap<>();

    for (Field field : getFields()) {
      values.put(field.getFieldName(), field.getValue());
    }

    // TODO: phuffer - should return immutable map?
    return values;
  }

  /** @return a map of this object's inner {@link Field}s' names and their sanitized values */
  @Override
  public Map<String, Object> getSanitizedValue() {
    Map<String, Object> values = new HashMap<>();

    for (Field field : getFields()) {
      values.put(field.getFieldName(), field.getSanitizedValue());
    }

    return values;
  }

  /**
   * Sets this {@link ObjectField}'s inner {@link Field} and their values.
   *
   * @param values a map of inner {@link Field}'s names to their values
   */
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

  /**
   * Validates every inner {@link Field} contained within this {@link ObjectField}.
   *
   * @return a list containing {@link ErrorMessage} if there were validation errors, otherwise an
   *     empty list
   */
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

  /**
   * Sets the path of each inner {@link Field} contained within this {@link ObjectField}.
   *
   * @param path the path to set on each inner {@link Field}
   */
  @Override
  public void setPath(List<Object> path) {
    super.setPath(path);
    getFields()
        .stream()
        .filter(Objects::nonNull)
        .forEach(child -> child.setPath(createInnerFieldPath(getPath(), child.getFieldName())));
  }

  /**
   * Returns a set of error codes containing the union any inner {@link Field}'s error codes and the
   * error codes from this {@link ObjectField}.
   *
   * @return a set of error codes that can be returned by this {@link Field}
   */
  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .addAll(
            getFields()
                .stream()
                .map(Field::getErrorCodes)
                .flatMap(Collection<String>::stream)
                .collect(Collectors.toList()))
        .build();
  }

  private List<Object> createInnerFieldPath(List<Object> path, String childName) {
    return new ImmutableList.Builder<>().addAll(path).add(childName).build();
  }
}
