/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.common.fields.base;

import static org.codice.ddf.admin.api.fields.Field.FieldBaseType.LIST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseListField<T extends Field> extends BaseField<List>
        implements ListField<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseListField.class);

    protected List<T> fields;

    protected T listFieldType;

    private boolean isRequiredNonEmpty;

    public BaseListField(String fieldName, String description, T listFieldType) {
        super(fieldName, null, description, LIST);
        this.fields = new ArrayList<>();
        this.listFieldType = listFieldType;
    }

    @Override
    public T getListFieldType() {
        return listFieldType;
    }

    @Override
    public List<T> getList() {
        return fields;
    }

    @Override
    public List getValue() {
        return fields.stream()
                .map(field -> field.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public void setValue(List values) {
        if (values == null || values.isEmpty()) {
            fields = null;
            return;
        }

        List<T> newFields = new ArrayList<T>();
        for (Object val : values) {
            try {
                T newField = (T) getListFieldType().getClass()
                        .newInstance();
                newField.setValue(val);
                add(newField);
            } catch (IllegalAccessException | InstantiationException e) {
                LOGGER.debug("Unable to create instance of fieldType {}",
                        getListFieldType().fieldTypeName());
            }
        }
    }

    @Override
    public BaseListField add(T value) {
        // TODO: 4/10/17 perform special validation for object fields
        // TODO: 4/10/17 Copy the obj instead
        value.isRequired(listFieldType.isRequired());
        fields.add(value);
        return this;
    }

    @Override
    public BaseListField addAll(List<T> values) {
        values.forEach(field -> add(field));
        return this;
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = super.validate();

        if (validationMsgs.isEmpty()) {
            List<Message> fieldValidationMsgs = getList().stream()
                    .map(field -> (List<Message>) field.validate())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            fieldValidationMsgs.forEach(msg -> msg.addSubpath(fieldName()));
            validationMsgs.addAll(fieldValidationMsgs);
        }

        return validationMsgs;
    }

    @Override
    public boolean isRequiredNonEmpty() {
        return isRequiredNonEmpty;
    }

    @Override
    public ListField<T> isRequiredNonEmpty(boolean required) {
        isRequiredNonEmpty = required;
        return this;
    }
}
