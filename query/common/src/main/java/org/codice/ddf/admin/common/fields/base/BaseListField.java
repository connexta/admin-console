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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseListField<T extends DataType> extends BaseDataType<List>
        implements ListField<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseListField.class);

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
        return elements.stream()
                .map(field -> field.getValue())
                .collect(Collectors.toList());
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
    public T createListEntry() {
        try {
            return getCreateListEntryCallable().call();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create new instance of list content for field: " + fieldName());
        }
    }

    @Override
    public BaseListField<T> add(T value) {
        T newElem = createListEntry();
        newElem.setValue(value.getValue());
        newElem.pathName(Integer.toString(elements.size()));
        newElem.updatePath(path());
        elements.add(newElem);
        return this;
    }

    @Override
    public BaseListField<T> addAll(Collection<T> values) {
        values.forEach(field -> add(field));
        return this;
    }

    @Override
    public List<ErrorMessage> validate() {
        List<ErrorMessage> validationMsgs = super.validate();

        if (validationMsgs.isEmpty() && (getList() != null)) {
            List<ErrorMessage> fieldValidationMsgs = getList().stream()
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
    public void updatePath(List<String> subPath) {
        super.updatePath(subPath);
        getList().forEach(field -> field.updatePath(path()));
    }

    @Override
    public void pathName(String pathName) {
        super.pathName(pathName);
        getList().forEach(field -> field.updatePath(path()));
    }

    @Override
    public Set<String> getErrorCodes() {
        Set<String> errors = super.getErrorCodes();
        errors.addAll(createListEntry().getErrorCodes());
        return errors;
    }

    public BaseListField<T> useDefaultRequired() {
        return this;
    }
}