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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.report.ErrorMessage;

public abstract class BaseListField<T extends Field> extends BaseField<List>
    implements ListField<T> {

  protected List<T> elements;

  public BaseListField(String fieldName) {
    super(fieldName, null, null);
    this.elements = new ArrayList<>();
  }

  public abstract Callable<T> getCreateListEntryCallable();

  @Override
  public List<T> getList() {
    return elements;
  }

  @Override
  public List getValue() {
    return elements.stream().map(Field::getValue).collect(Collectors.toList());
  }

  @Override
  public List getSanitizedValue() {
    return elements.stream().map(Field::getSanitizedValue).collect(Collectors.toList());
  }

  @Override
  public void setValue(List values) {
    if (values == null || values.isEmpty()) {
      elements.clear();
      return;
    }

    for (Object val : values) {
      T newField = createListEntry();
      newField.setValue(val);
      add(newField);
    }
  }

  @Override
  @SuppressWarnings("squid:S00112" /* Throwing RuntimeException intentionally */)
  public T createListEntry() {
    try {
      return getCreateListEntryCallable().call();
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to create new instance of list content for field: " + getFieldName());
    }
  }

  @Override
  public BaseListField<T> add(T value) {
    T newElem = createListEntry();
    newElem.setValue(value.getValue());
    elements.add(newElem);
    return this;
  }

  @Override
  public BaseListField<T> addAll(Collection<T> values) {
    values.forEach(this::add);
    return this;
  }

  @Override
  public List<ErrorMessage> validate() {
    List<ErrorMessage> validationMsgs = super.validate();

    if (validationMsgs.isEmpty() && (getList() != null)) {
      List<ErrorMessage> fieldValidationMsgs =
          getList()
              .stream()
              .map(field -> (List<ErrorMessage>) field.validate())
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
      validationMsgs.addAll(fieldValidationMsgs);
    }

    return validationMsgs;
  }

  @Override
  public BaseListField<T> isRequired(boolean required) {
    super.isRequired(required);
    return this;
  }

  @Override
  public void setPath(List<Object> path) {
    super.setPath(path);
    for (int i = 0; i < getList().size(); i++) {
      getList().get(i).setPath(createElemPath(getPath(), i));
    }
  }

  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .addAll(createListEntry().getErrorCodes())
        .build();
  }

  private List<Object> createElemPath(List<Object> path, int index) {
    return new ImmutableList.Builder<>().addAll(path).add(index).build();
  }

  public BaseListField<T> useDefaultRequired() {
    return this;
  }
}
