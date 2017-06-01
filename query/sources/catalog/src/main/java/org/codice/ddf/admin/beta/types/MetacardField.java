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
package org.codice.ddf.admin.beta.types;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.FloatField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.MetacardType;

public class MetacardField extends BaseObjectField {

    public static final String FIELD_NAME = "metacard";

    public static final String FIELD_TYPE_NAME = "Metacard";

    public static final String DESCRIPTION = "A Metacard containing a list of attributes.";

    private List<Field> fields = Collections.emptyList();

    private Map<String, String> descriptorNameToFieldMapping;

    public MetacardField() {
        this(FIELD_NAME);
    }

    private MetacardField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
        descriptorNameToFieldMapping = new HashMap<>();
    }

    public MetacardField(List<MetacardType> metacardTypes) {
        this(FIELD_NAME);
        Map<String, Boolean> found = new HashMap<>();

        fields = metacardTypes.stream()
                .map(MetacardType::getAttributeDescriptors)
                .flatMap(Collection::stream)
                .filter((descriptor) -> {
                    if (found.get(descriptor.getName()) != null) {
                        return false;
                    }
                    found.put(descriptor.getName(), true);
                    return true;
                })
                .map(this::createField)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    private Field mapType(String fieldName, AttributeType.AttributeFormat format) {
        switch (format) {
        case BOOLEAN:
            return new BooleanField(fieldName);
        case STRING:
        case BINARY:
            return new StringField(fieldName);
        case XML:
            return new XmlField(fieldName);
        case GEOMETRY:
            return new GeometryField(fieldName);
        case DATE:
            return new DateField(fieldName);
        case DOUBLE:
        case FLOAT:
            return new FloatField(fieldName);
        case LONG:
        case INTEGER:
        case SHORT:
            return new IntegerField(fieldName);
        default:
            return null;
        }
    }

    private Field createField(AttributeDescriptor descriptor) {
        String fieldName = descriptor.getName()
                .replace("-", "")
                .replace(".", ""); // graphql doesn't like these characters
        descriptorNameToFieldMapping.put(descriptor.getName(), fieldName);
        Field field = mapType(fieldName,
                descriptor.getType()
                        .getAttributeFormat());
        if (field != null && descriptor.isMultiValued()) {
            return new ListFieldImpl(fieldName, field.getClass());
        }
        return field;
    }
}
