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

import static org.codice.ddf.admin.api.DataType.FieldBaseType.LIST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.report.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListFieldImpl<T extends DataType> extends BaseDataType<List>
        implements ListField<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListFieldImpl.class);

    public static final String DEFAULT_FIELD_NAME  = "list";

    protected List<T> fields;

    protected T listFieldType;

    public ListFieldImpl(String fieldName, Class<T> listFieldType) {
        super(fieldName, null, null, LIST);
        this.fields = new ArrayList<>();
        try {
            this.listFieldType = listFieldType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(String.format("Unable to create new instance of class [%s]. Ensure there is a default constructor for the ListFieldImpl to initialize.", listFieldType.getClass().getCanonicalName()));
        }
    }

    public ListFieldImpl(String fieldName, T listFieldType) {
        super(fieldName, null, null, LIST);
        this.fields = new ArrayList<>();
        this.listFieldType = listFieldType;
    }

    public ListFieldImpl(Class<T> listFieldType) {
        this(DEFAULT_FIELD_NAME, listFieldType);
    }

    public ListFieldImpl(T listFieldType) {
        this(DEFAULT_FIELD_NAME, listFieldType);
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
            fields.clear();
            return;
        }

        for (Object val : values) {
            try {
                T newField = (T) getListFieldType().getClass()
                        .newInstance();
                newField.setValue(val);
                add(newField);
            } catch (IllegalAccessException | InstantiationException e) {
                LOGGER.debug("Unable to create instance of fieldType {}",
                        getListFieldType().fieldTypeName());
                throw new RuntimeException("Unable to create instance of fieldType {}" + getListFieldType().fieldTypeName());
            }
        }
    }

    @Override
    public ListFieldImpl<T> add(T value) {
        value.matchRequired(listFieldType);
        value.fieldName(INDEX_DELIMETER + fields.size());
        value.updatePath(path());
        fields.add(value);
        return this;
    }

    @Override
    public ListFieldImpl<T> addAll(Collection<T> values) {
        values.forEach(field -> add(field));
        return this;
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = super.validate();

        if (validationMsgs.isEmpty() && (getList() != null)) {
            List<Message> fieldValidationMsgs = getList().stream()
                    .map(field -> (List<Message>) field.validate())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            validationMsgs.addAll(fieldValidationMsgs);
        }

        return validationMsgs;
    }

    @Override
    public ListField<T> matchRequired(DataType fieldToMatch) {
        super.matchRequired(fieldToMatch);
        listFieldType.matchRequired(((ListField) fieldToMatch).getListFieldType());
        getList().forEach(field -> field.matchRequired(listFieldType));
        return this;
    }

    @Override
    public ListFieldImpl<T> isRequired(boolean required) {
        super.isRequired(required);
        return this;
    }

    @Override
    public void updatePath(List<String> subPath) {
        super.updatePath(subPath);
        getList().forEach(field -> field.updatePath(path()));
    }

    @Override
    public void fieldName(String fieldName) {
        super.fieldName(fieldName);
        getList().forEach(field -> field.updatePath(path()));
    }
}