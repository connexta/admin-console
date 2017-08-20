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

import java.util.List;
import java.util.Objects;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public abstract class BaseFieldProvider extends BaseObjectField implements FieldProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFieldProvider.class);

    private static final String BINDING_FUNCTION = "Binding function to field provider %s";
    private static final String UNBINDING_FUNCTION = "Unbinding function from field provider %s";

    public BaseFieldProvider(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description);
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of();
    }

    @Override
    public void updateInnerFieldPaths() {
        super.updateInnerFieldPaths();
        getDiscoveryFunctions().stream()
                .filter(Objects::nonNull)
                .forEach(child -> child.updatePath(path()));
        getMutationFunctions().stream()
                .filter(Objects::nonNull)
                .forEach(child -> child.updatePath(path()));
    }

    public FunctionField getDiscoveryFunction(String name) {
        return getDiscoveryFunctions().stream()
                .filter(funcField -> funcField.getName()
                        .equals(name))
                .findFirst()
                .orElse(null);
    }

    public FunctionField getMutationFunction(String name) {
        return getMutationFunctions().stream()
                .filter(funcField -> funcField.getName()
                        .equals(name))
                .findFirst()
                .orElse(null);
    }

    public void bindField(FunctionField functionField) {
        ServiceCommons.updateGraphQLSchema(getClass(), String.format(BINDING_FUNCTION, getTypeName()));
    }

    public void unbindField(FunctionField functionField) {
        ServiceCommons.updateGraphQLSchema(getClass(), String.format(UNBINDING_FUNCTION, getTypeName()));
    }
}
