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
package org.codice.ddf.admin.common.fields.base.function;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFieldProvider extends BaseObjectField implements FieldProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFieldProvider.class);

    public BaseFieldProvider(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description);
    }

    @Override
    public List<Field> getFields() {
        List<FunctionField> mutations = getMutationFunctions() == null ? new ArrayList() : getMutationFunctions();
        List<Field> discoveryFields = getDiscoveryFields() == null ? new ArrayList() : getDiscoveryFields();

        return Stream.concat(mutations.stream(), discoveryFields.stream())
                .collect(Collectors.toList());
    }

    public Field getDiscoveryField(String fieldName) {
        return getDiscoveryFields().stream()
                .filter(field -> field.fieldName()
                        .equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    public Field getMutationFunction(String fieldName) {
        return getMutationFunctions().stream()
                .filter(field -> field.fieldName()
                        .equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    public void bindField(Field field) {
        ServiceCommons.updateGraphQLSchema(getClass());
    }

    public void unbindField(Field field) {
        ServiceCommons.updateGraphQLSchema(getClass());
    }
}
