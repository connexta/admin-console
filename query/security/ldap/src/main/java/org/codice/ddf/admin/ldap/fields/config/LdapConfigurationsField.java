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

import org.codice.ddf.admin.common.fields.base.BaseListField;

public class LdapConfigurationsField extends BaseListField<LdapConfigurationField> {

    public static final String DEFAULT_FIELD_NAME = "configs";

    public static final String DESCRIPTION = "A list of LDAP configurations.";

    public LdapConfigurationsField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new LdapConfigurationField().allFieldsRequired(true));
    }

    public LdapConfigurationsField(LdapConfigurationField listFieldType) {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, listFieldType);
    }

    @Override
    public LdapConfigurationsField add(LdapConfigurationField value) {
        super.add(value);
        return this;
    }

    public LdapConfigurationsField addAll(List<LdapConfigurationField> configs) {
        configs.forEach(config -> add(config));
        return this;
    }
}
