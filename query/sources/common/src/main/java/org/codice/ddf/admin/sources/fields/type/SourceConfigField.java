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
package org.codice.ddf.admin.sources.fields.type;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.fields.common.UrlField;

import com.google.common.collect.ImmutableList;

public abstract class SourceConfigField extends BaseObjectField {

    public static final String SOURCE_NAME_FIELD_NAME = "sourceName";

    public static final String ENDPOINT_URL_FIELD_NAME = "endpointUrl";

    private PidField pidField;

    private StringField sourceName;

    private UrlField endpointUrl;

    private CredentialsField creds;

    protected SourceConfigField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description);

        pidField = new PidField();
        sourceName = new StringField(SOURCE_NAME_FIELD_NAME);
        endpointUrl = new UrlField(ENDPOINT_URL_FIELD_NAME);
        creds = new CredentialsField();
    }

    // Setters

    public SourceConfigField pid(String servicePid) {
        this.pidField.setValue(servicePid);
        return this;
    }

    public SourceConfigField sourceName(String sourceName) {
        this.sourceName.setValue(sourceName);
        return this;
    }

    public SourceConfigField endpointUrl(String url) {
        this.endpointUrl.setValue(url);
        return this;
    }

    public SourceConfigField credentials(String username, String password) {
        this.creds.username(username)
                .password(password);
        return this;
    }

    // Getters
    public String sourceName() {
        return sourceName.getValue();
    }

    public StringField sourceNameField() {
        return sourceName;
    }

    public CredentialsField credentials() {
        return this.creds;
    }

    public String pid() {
        return pidField.getValue();
    }

    public PidField pidField() {
        return pidField;
    }

    public String endpointUrl() {
        return endpointUrl.getValue();
    }

    public UrlField endpointUrlField() {
        return endpointUrl;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(pidField, sourceName, endpointUrl, creds);
    }
}
