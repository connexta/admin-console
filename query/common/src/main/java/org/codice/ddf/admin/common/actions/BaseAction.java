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
package org.codice.ddf.admin.common.actions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAction<T extends Field> implements Action<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);
    private String name;
    private String description;
    private T returnType;

    public BaseAction(String name, String description, T returnType) {
        this.name = name;
        this.description = description;
        this.returnType = returnType;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public T returnType() {
        return returnType;
    }

    @Override
    public void setArguments(Map<String, Object> args) {

        if(args == null || args.isEmpty()) {
            return;
        }

        getArguments().stream()
                .filter(field -> args.containsKey(field.fieldName()))
                .forEach(field -> field.setValue(args.get(field.fieldName())));

        // TODO: tbatie - 3/16/17 - Add logger if a fieldName is not found
//        for(Field arg : args) {
//            Optional<Field> matchedField = getArguments().stream()
//                    .filter(field -> arg.fieldName() != null && field.fieldName().equals(arg.fieldName()))
//                    .findFirst();
//
//            if(matchedField.isPresent()) {
//                matchedField.get().setEnumValue(arg.getValue());
//            } else {
//                List<String> allFieldNames = getArguments().stream()
//                        .map(field -> field.fieldName())
//                        .collect(Collectors.toList());
//
//                LOGGER.debug("Unknown argument field name {} in action {}. Field names must be one of: [{}]", arg.fieldName(), name, String.join(",", allFieldNames));
//            }
//        }
    }

    @Override
    public List<Message> validate() {
        return getArguments().stream()
                .map(Field::validate)
                .flatMap(Collection<Message>::stream)
                .collect(Collectors.toList());
    }
}
