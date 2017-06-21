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
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;

import com.google.common.collect.ImmutableList;

public class LdapConfigurationField extends BaseObjectField {
    public static final String DEFAULT_FIELD_NAME = "config";

    public static final String FIELD_TYPE_NAME = "LdapConfiguration";

    public static final String DESCRIPTION =
            "A configuration containing all the required fields for saving LDAP settings";

    public static final String CLAIMS_MAPPING = "claimsMapping";

    private PidField pid;

    private LdapConnectionField connection;

    private LdapBindUserInfo bindUserInfo;

    private LdapDirectorySettingsField settings;

    private ListField<ClaimsMapEntry> claimMappings;

    public LdapConfigurationField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        pid = new PidField();
        connection = new LdapConnectionField();
        bindUserInfo = new LdapBindUserInfo();
        settings = new LdapDirectorySettingsField();
        claimMappings = new ListFieldImpl<>(CLAIMS_MAPPING, ClaimsMapEntry.class);

        updateInnerFieldPaths();
    }

    //Field getters
    public LdapConnectionField connectionField() {
        return connection;
    }

    public LdapBindUserInfo bindUserInfoField() {
        return bindUserInfo;
    }

    public LdapDirectorySettingsField settingsField() {
        return settings;
    }

    public PidField pidField() {
        return pid;
    }

    public ListField<ClaimsMapEntry> claimMappingsField() {
        return claimMappings;
    }

    //Value getters
    public String pid() {
        return pid.getValue();
    }

    public Map<String, String> claimsMapping() {
        return claimMappings.getList()
                .stream()
                .collect(Collectors.toMap(ClaimsMapEntry::key, ClaimsMapEntry::value));
    }

    //Value setters
    public LdapConfigurationField pid(String pid) {
        this.pid.setValue(pid);
        return this;
    }

    public LdapConfigurationField connection(LdapConnectionField connection) {
        this.connection.setValue(connection.getValue());
        return this;
    }

    public LdapConfigurationField bindUserInfo(LdapBindUserInfo bindUserInfo) {
        this.bindUserInfo.setValue(bindUserInfo.getValue());
        return this;
    }

    public LdapConfigurationField settings(LdapDirectorySettingsField settings) {
        this.settings.setValue(settings.getValue());
        return this;
    }

    public LdapConfigurationField mapClaim(String claim, String attribute) {
        claimMappings.add(new ClaimsMapEntry().key(claim)
                .value(attribute));
        return this;
    }

    public LdapConfigurationField mapAllClaims(Map<String, String> mapping) {
        mapping.entrySet()
                .forEach(entry -> claimMappings.add(new ClaimsMapEntry().key(entry.getKey())
                        .value(entry.getValue())));
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(pid, connection, bindUserInfo, settings, claimMappings);
    }

    @Override
    public LdapConfigurationField allFieldsRequired(boolean required) {
        super.allFieldsRequired(required);
        return this;
    }

    public LdapConfigurationField useDefaultRequired() {
        connection.useDefaultRequired();
        bindUserInfo.useDefaultRequired();
        settings.useDefaultRequiredForAuthentication();
        isRequired(true);
        return this;
    }
}
