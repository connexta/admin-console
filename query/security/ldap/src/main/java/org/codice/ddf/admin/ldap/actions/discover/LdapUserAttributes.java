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
package org.codice.ddf.admin.ldap.actions.discover;

import static org.codice.ddf.admin.ldap.sample.SampleFields.SAMPLE_LDAP_ATTRIBUTE;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.security.common.fields.ldap.LdapConfigurationField;
import org.codice.ddf.admin.security.common.fields.ldap.query.LdapAttributeListField;

import com.google.common.collect.ImmutableList;

public class LdapUserAttributes extends BaseAction<LdapAttributeListField> {

    public static final String NAME = "userAttributes";

    public static final String DESCRIPTION =
            "Retrieves a subset of available user attributes based on the LDAP settings provided.";

    private LdapConfigurationField config = new LdapConfigurationField();

    public LdapUserAttributes() {
        super(NAME, DESCRIPTION, new LdapAttributeListField());
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public LdapAttributeListField performAction() {
        return new LdapAttributeListField().add(SAMPLE_LDAP_ATTRIBUTE)
                .add(SAMPLE_LDAP_ATTRIBUTE);
    }
}
