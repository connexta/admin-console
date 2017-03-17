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
package org.codice.ddf.admin.query.sources.common;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.BaseUnionField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;
import org.codice.ddf.admin.query.commons.fields.common.CredentialsField;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.commons.fields.common.UrlField;
import org.codice.ddf.admin.query.sources.common.fields.CswSourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.fields.OpensearchSourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.fields.WfsSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SourceConfigUnionField extends BaseUnionField {

    public static final String FIELD_NAME = "sourceConfig";
    public static final String FIELD_TYPE_NAME = "SourceConfiguration";
    public static final String DESCRIPTION = "All supported source configuration types";
    private static final List<ObjectField> UNION_TYPES = ImmutableList.of(new CswSourceConfigurationField(), new WfsSourceConfigurationField(), new OpensearchSourceConfigurationField());

    protected PidField id =  new PidField();
    protected StringField sourceName = new StringField("sourceName");
    protected UrlField endpointUrl = new UrlField("endpointUrl");
    protected CredentialsField creds = new CredentialsField();

    public SourceConfigUnionField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION, UNION_TYPES, false);
    }

    protected SourceConfigUnionField(String fieldTypeName, String description) {
        super(FIELD_NAME, fieldTypeName, description, UNION_TYPES, true);
    }

    public SourceConfigUnionField id(String id) {
        this.id.setValue(id);
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
        this.creds.username(username).password(password);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(id, sourceName, endpointUrl, creds);
    }
}
