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
 */
package org.codice.ddf.admin.sources.fields;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class WfsVersion extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "wfsVersion";

    public static final String TYPE_NAME = "WfsVersion";

    public static final String DESCRIPTION =
            "The WFS version number specifying the specification version for the client and server to operate with. The version number contains three non-negative integers in the form \"x.y.z\" where y and z shall not exceed 99. Refer to OGC 06-121r3 section 7.3.1 for more information.";

    public static final String WFS_VERSION_1 = "1.0.0";

    public static final String WFS_VERSION_2 = "2.0.0";

    public static final String WFS_1_FIELD_NAME = "WFS_1";

    public static final String WFS_2_FIELD_NAME = "WFS_2";

    public WfsVersion() {
        this(null);
    }

    public WfsVersion(Field<String> wfsVersion) {
        this(DEFAULT_FIELD_NAME,
                TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new Wfs1(), new Wfs2()),
                wfsVersion);
    }

    public WfsVersion(String fieldName, String fieldTypeName, String description,
            List<Field<String>> enumValues, Field<String> enumValue) {
        super(fieldName, fieldTypeName, description, enumValues, enumValue);
    }

    protected static final class Wfs1 extends StringField {

        public static final String DESCRIPTION =
                "Indicates a server implements version 1.0.0 of the WFS specification.";

        public Wfs1() {
            super(WFS_1_FIELD_NAME, TYPE_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return WFS_VERSION_1;
        }
    }

    protected static final class Wfs2 extends StringField {

        public static final String DESCRIPTION =
                "Indicates a server implements version 2.0.0 of the WFS specification.";

        public Wfs2() {
            super(WFS_2_FIELD_NAME, TYPE_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return WFS_VERSION_2;
        }
    }
}
