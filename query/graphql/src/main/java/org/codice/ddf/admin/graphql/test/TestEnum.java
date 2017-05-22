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
package org.codice.ddf.admin.graphql.test;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class TestEnum extends BaseEnumField<String> {
    public static final String DEFAULT_FIELD_NAME = "authType";

    public static final String FIELD_TYPE_NAME = "AuthenticationType";

    public static final String DESCRIPTION =
            "Defines a specific type of authentication that should be performed.";

    public TestEnum() {
        this(null);
    }

    protected TestEnum(DataType<String> enumType) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new EnumA(), new EnumB()),
                enumType);
    }

    protected static final class EnumA extends StringField {
        public static final String ENUM_A = "ENUM_A";

        public EnumA() {
            super(ENUM_A);
        }

        @Override
        public String getValue() {
            return ENUM_A;
        }
    }

    protected static final class EnumB extends StringField {
        public static final String ENUM_B = "ENUM_B";

        public EnumB() {
            super(ENUM_B);
        }

        @Override
        public String getValue() {
            return ENUM_B;
        }
    }
}
