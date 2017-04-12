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
package org.codice.ddf.admin.ldap.fields.config;

import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.security.common.fields.sts.StsClaimField;

import com.google.common.collect.ImmutableList;

public class LdapAttributeEntryField extends BaseObjectField {

    public static final String FIELD_NAME = "attributeMapping";

    public static final String FIELD_TYPE_NAME = "AttributeMapping";

    public static final String DESCRIPTION = "A mapping from an STS claim to a user attribute.";

    public static final String STS_CLAIM = "stsClaim";

    public static final String USER_ATTRIBUTE = "userAttribute";

    private StsClaimField stsClaim;

    private StringField userAttribute;

    public LdapAttributeEntryField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        stsClaim = new StsClaimField(STS_CLAIM);
        userAttribute = new StringField(USER_ATTRIBUTE);
    }

    public LdapAttributeEntryField stsClaim(String claim) {
        stsClaim.setValue(claim);
        return this;
    }

    public LdapAttributeEntryField userAttribute(String userAttribute) {
        this.userAttribute.setValue(userAttribute);
        return this;
    }

    public String stsClaim(){
        return stsClaim.getValue();
    }

    public String userAttribute() {
        return userAttribute.getValue();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(stsClaim, userAttribute);
    }
}
