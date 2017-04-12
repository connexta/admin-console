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

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurationField extends BaseObjectField {

    public static final String FIELD_NAME = "config";

    public static final String FIELD_TYPE_NAME = "LdapConfiguration";

    public static final String DESCRIPTION =
            "A configuration containing all the required fields for saving LDAP settings";

    private PidField pid;

    private LdapConnectionField connection;

    private LdapBindUserInfo bindUserInfo;

    private LdapSettingsField settings;

    public LdapConfigurationField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.pid = new PidField();
        this.connection = new LdapConnectionField();
        this.bindUserInfo = new LdapBindUserInfo();
        this.settings = new LdapSettingsField();
    }

    public LdapConfigurationField connection(LdapConnectionField connection) {
        this.connection.setValue(connection.getValue());
        return this;
    }

    public LdapConfigurationField bindUserInfo(LdapBindUserInfo bindUserInfo) {
        this.bindUserInfo.setValue(bindUserInfo.getValue());
        return this;
    }

    public LdapConfigurationField settings(LdapSettingsField settings) {
        this.settings.setValue(settings.getValue());
        return this;
    }

    //Field getters
    // TODO: tbatie - 4/11/17 - Rename these to -field
    public LdapConnectionField connection() {
        return connection;
    }

    public LdapBindUserInfo bindUserInfo() {
        return bindUserInfo;
    }

    public LdapSettingsField settings() {
        return settings;
    }

    //Value getters
    public String pid() {
        return pid.getValue();
    }

    //Setters
    public LdapConfigurationField pid(String pid) {
        this.pid.setValue(pid);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(pid, connection, bindUserInfo, settings);
    }
}
