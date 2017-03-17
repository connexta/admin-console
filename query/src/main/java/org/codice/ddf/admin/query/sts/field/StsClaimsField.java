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
package org.codice.ddf.admin.query.sts.field;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class StsClaimsField extends BaseListField<StsClaimField> {

    public static final String DEFAULT_FIELD_NAME = "claims";
    public static final String DESCRIPTION = "All currently configured claims supported by the STS";

    public StsClaimsField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new StsClaimField());
    }

    @Override
    public StsClaimsField add(StsClaimField value) {
        super.add(value);
        return this;
    }
}
