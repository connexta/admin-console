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
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import ddf.catalog.data.MetacardType;

public class MetacardAttributeField extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "attribute";

    public static final String TYPE_NAME = "MetacardAttribute";

    public static final String DESCRIPTION = "Name of a metacard attribute.";

    public MetacardAttributeField() {
        this(Collections.emptyList());
    }

    public MetacardAttributeField(List<MetacardType> metacardTypes) {
        super(DEFAULT_FIELD_NAME, TYPE_NAME, DESCRIPTION, createEnumerations(metacardTypes));
    }

    private static List<DataType<String>> createEnumerations(List<MetacardType> metacardTypes) {
        Map<String, Object> found = new HashMap<>();

        List<DataType<String>> types = metacardTypes.stream()
                .map(MetacardType::getAttributeDescriptors)
                .flatMap(Collection::stream)
                .filter((descriptor) -> {
                    if (found.get(descriptor.getName()) != null) {
                        return false;
                    }
                    found.put(descriptor.getName(), true);
                    return true;
                })
                .map(descriptor -> {
                    String fieldName = descriptor.getName()
                            .replace("-", "")
                            .replace(".", "");
                    StringField enumValue = new StringField(fieldName);
                    enumValue.setValue(descriptor.getName());
                    return enumValue;
                })
                .collect(Collectors.toList());

        types.add(new AnyText());

        return types;
    }

    public static class AnyText extends StringField {
        public static final String ANY_TEXT = "anyText";

        public static final String FIELD_NAME = ANY_TEXT;

        public static final String FIELD_TYPE = ANY_TEXT;

        public static final String DESCRIPTION = "Provides guest access.";

        public AnyText() {
            super(FIELD_NAME, FIELD_TYPE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return ANY_TEXT;
        }
    }
}
