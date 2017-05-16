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

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.common.fields.base.BaseUnionField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.fields.common.UrlField;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class SourceConfigUnionField extends BaseUnionField {

    public static final String FIELD_NAME = "sourceConfig";

    public static final String FIELD_TYPE_NAME = "SourceConfiguration";

    public static final String DESCRIPTION = "All supported source configuration types.";

    public static final String SOURCE_NAME_FIELD_NAME = "sourceName";

    public static final String ENDPOINT_URL_FIELD_NAME = "endpointUrl";

    private static final List<ObjectField> UNION_TYPES =
            ImmutableList.of(new CswSourceConfigurationField(),
                    new WfsSourceConfigurationField(),
                    new OpenSearchSourceConfigurationField());

    protected PidField pidField;

    protected StringField sourceName;

    protected UrlField endpointUrl;

    protected CredentialsField creds;

    public SourceConfigUnionField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION, UNION_TYPES, false);
    }

    protected SourceConfigUnionField(String fieldTypeName, String description) {
        super(FIELD_NAME, fieldTypeName, description, UNION_TYPES, true);
    }

    // Setters

    public SourceConfigUnionField pid(String servicePid) {
        this.pidField.setValue(servicePid);
        return this;
    }

    public SourceConfigUnionField sourceName(String sourceName) {
        this.sourceName.setValue(sourceName);
        return this;
    }

    public SourceConfigUnionField endpointUrl(String url) {
        this.endpointUrl.setValue(url);
        return this;
    }

    public SourceConfigUnionField credentials(String username, String password) {
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

    @Override
    public void initializeFields() {
        pidField = new PidField();
        sourceName = new StringField(SOURCE_NAME_FIELD_NAME);
        endpointUrl = new UrlField(ENDPOINT_URL_FIELD_NAME);
        creds = new CredentialsField();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add(PidField.DEFAULT_FIELD_NAME, pid())
                .add(SOURCE_NAME_FIELD_NAME, sourceName())
                .add(ENDPOINT_URL_FIELD_NAME, endpointUrl())
                .add(CredentialsField.USERNAME_FIELD_NAME, credentials().username())
                .add(CredentialsField.PASSWORD_FIELD_NAME, FLAG_PASSWORD)
                .toString();
    }
}
