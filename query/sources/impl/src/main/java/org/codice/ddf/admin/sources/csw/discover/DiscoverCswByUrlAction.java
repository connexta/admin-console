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
 */
package org.codice.ddf.admin.sources.csw.discover;

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.createSourceInfoField;
import static org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField.ENDPOINT_URL_FIELD_NAME;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.ReportWithResult;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.commons.utils.CswSourceUtils;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

import com.google.common.collect.ImmutableList;

public class DiscoverCswByUrlAction extends BaseAction<SourceInfoField> {

    public static final String ID = "discoverCswByUrl";

    public static final String DESCRIPTION =
            "Attempts to discover a CSW source given a URL, and optional username and password.";

    public static final String URL_FIELD_NAME = ENDPOINT_URL_FIELD_NAME;

    private UrlField endpointUrl;

    private CredentialsField credentialsField;

    private CswSourceUtils cswSourceUtils;

    public DiscoverCswByUrlAction() {
        super(ID, DESCRIPTION, new SourceInfoField());
        credentialsField = new CredentialsField();
        endpointUrl = new UrlField(URL_FIELD_NAME);
        endpointUrl.isRequired(true);
        cswSourceUtils = new CswSourceUtils();
    }

    public DiscoverCswByUrlAction(CswSourceUtils cswSourceUtils) {
        this();
        this.cswSourceUtils = cswSourceUtils;
    }

    @Override
    public SourceInfoField performAction() {
        ReportWithResult<SourceConfigUnionField> configResult = cswSourceUtils.getPreferredCswConfig(endpointUrl, credentialsField);
        addMessages(configResult);
        if(containsErrorMsgs()) {
            return null;
        }
        return createSourceInfoField(true, configResult.result());
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(endpointUrl, credentialsField);
    }
}
