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
package org.codice.ddf.admin.security.common.fields.wcpm;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class ClaimsMapEntry extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "claimsMapping";
    public static final String FIELD_TYPE_NAME = "ClaimsMapEntry";
    public static final String DESCRIPTION = "Represents a mapping of a claim subject to a specific claim value";

    private StringField claim;
    private StringField claimValue;

    public ClaimsMapEntry() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        claim = new StringField("claim");
        claimValue = new StringField("claimValue");
    }

    public ClaimsMapEntry claim(String claim) {
        this.claim.setValue(claim);
        return this;
    }

    public ClaimsMapEntry claimValue(String claimValue) {
        this.claimValue.setValue(claimValue);
        return this;
    }


    @Override
    public List<Field> getFields() {
        return ImmutableList.of(claim, claimValue);
    }
}
