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
package org.codice.ddf.admin.security.common.fields.sts;

import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class StsClaimField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "claim";

    public static final String FIELD_NAME_TYPE = "StsClaim";

    public static final String DESCRIPTION =
            "A statement that one subject, such as a person or organization, makes about itself or another subject";

    public StsClaimField(String fieldName) {
        super(fieldName, FIELD_NAME_TYPE, DESCRIPTION);
    }

    public StsClaimField() {
        super(DEFAULT_FIELD_NAME, FIELD_NAME_TYPE, DESCRIPTION);
    }

    @Override
    public StsClaimField isRequired(boolean required) {
        super.isRequired(required);
        return this;
    }
}
