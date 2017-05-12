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
package org.codice.ddf.admin.sources.csw.discover;

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.createSourceInfoField;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.ReportWithResult;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.commons.utils.CswSourceUtils;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

import com.google.common.collect.ImmutableList;

public class DiscoverCswAction extends BaseAction<SourceInfoField> {

    public static final String ID = "discoverCsw";

    public static final String DESCRIPTION =
            "Attempts to discover a CSW source using the given hostname "
                    + "and port or URL. Either the hostname and port are required, or the URL is required. If both sets "
                    + "are provided, then the discovery will be attempted with the URL. If no arguments are given, "
                    + "then a missing required field error on the URL field will be returned.";

    private CredentialsField credentials;

    private AddressField address;

    private CswSourceUtils cswSourceUtils;

    public DiscoverCswAction() {
        super(ID, DESCRIPTION, new SourceInfoField());
        credentials = new CredentialsField();
        address = new AddressField();
        address.isRequired(true);
        cswSourceUtils = new CswSourceUtils();
    }

    public DiscoverCswAction(CswSourceUtils cswSourceUtils) {
        this();
        this.cswSourceUtils = cswSourceUtils;
    }

    @Override
    public SourceInfoField performAction() {
        ReportWithResult<SourceConfigUnionField> configResult;
        if (address.url() != null) {
            configResult = cswSourceUtils.getPreferredCswConfig(address.urlField(), credentials);
            addMessages(configResult);
            if (containsErrorMsgs()) {
                return null;
            }
        } else {
            ReportWithResult<UrlField> discoveredUrl = cswSourceUtils.discoverCswUrl(address.host(), credentials);
            addMessages(discoveredUrl);
            if (containsErrorMsgs()) {
                return null;
            }

            configResult = cswSourceUtils.getPreferredCswConfig(discoveredUrl.result(), credentials);
            addMessages(configResult);
            if (containsErrorMsgs()) {
                return null;
            }
        }
        return createSourceInfoField(true, configResult.result());
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(credentials, address);
    }
}
