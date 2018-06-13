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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.missingRequiredFieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.report.message.DefaultMessages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Handles common operations for all {@link Field}s that extend this class, such as basic
 * validation.
 *
 * @param <T> type of value contained by this field
 */
public abstract class BaseField<T> implements Field<T> {

  private String name;

  private String typeName;

  private String description;

  private List<Object> path;

  private boolean isRequired;

  private T value;

  public BaseField(String name, String typeName, String description) {
    this.name = name;
    this.typeName = typeName;
    this.description = description;
    path = new ArrayList<>();
    isRequired = false;
  }

  @Override
  public String getFieldName() {
    return name;
  }

  @Override
  public String getFieldType() {
    return typeName;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Set<String> getErrorCodes() {
    return ImmutableSet.of(DefaultMessages.MISSING_REQUIRED_FIELD);
  }

  @Override
  public T getValue() {
    return value;
  }

  @Override
  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public boolean isRequired() {
    return isRequired;
  }

  @Override
  public BaseField<T> isRequired(boolean required) {
    isRequired = required;
    return this;
  }

  /**
   * If this field is required and a value is not present, a {@link
   * DefaultMessages#MISSING_REQUIRED_FIELD} error will be added to this field's errors. If the
   * field's value is of type {@code java.util.List}, a {@link
   * DefaultMessages#MISSING_REQUIRED_FIELD} will be added to this field's errors if it is empty.
   *
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("squid:S3923" /* Not combining if and else if statements for clarity */)
  public List<ErrorMessage> validate() {
    List<ErrorMessage> errors = new ArrayList<>();

    if (isRequired()) {
      if (getValue() == null) {
        errors.add(missingRequiredFieldError(getPath()));
        // TODO: phuffer - Move this list check to BaseListField
      } else if (getValue() instanceof List && ((List) getValue()).isEmpty()) {
        errors.add(missingRequiredFieldError(getPath()));
      }
    }

    return errors;
  }

  @Override
  public List<Object> getPath() {
    return new ImmutableList.Builder().addAll(path).build();
  }

  @Override
  public void setPath(List<Object> path) {
    this.path.clear();
    this.path.addAll(path);
  }
}
