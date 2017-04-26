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
package org.codice.ddf.admin.sources.opensearch.discover;

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.createSourceInfoField;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.commons.utils.OpenSearchSourceUtils;
import org.codice.ddf.admin.common.Result;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

import com.google.common.collect.ImmutableList;

public class DiscoverOpenSearchByAddressAction extends BaseAction<SourceInfoField> {

    public static final String ID = "discoverOpenSearchByAddress";

    public static final String DESCRIPTION =
            "Attempts to discover OpenSearch sources with the given hostname and port, and optionally a username and password if authentication is enabled.";

    private AddressField addressField;

    private CredentialsField credentialsField;

    private OpenSearchSourceUtils openSearchSourceUtils;

    public DiscoverOpenSearchByAddressAction() {
        super(ID, DESCRIPTION, new SourceInfoField());
        openSearchSourceUtils = new OpenSearchSourceUtils();
        addressField = new AddressField();
        credentialsField = new CredentialsField();

        addressField.allFieldsRequired(true);
        credentialsField.allFieldsRequired(false);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(addressField, credentialsField);
    }

    @Override
    public SourceInfoField performAction() {
        Result<UrlField> discoveredUrl = openSearchSourceUtils.discoverOpenSearchUrl(addressField, credentialsField);

        if(discoveredUrl.isNotPresent()) {
            addArgumentMessages(discoveredUrl.argumentMessages());
            return null;
        }

        Result<SourceConfigUnionField> configResult = openSearchSourceUtils.getOpenSearchConfig(discoveredUrl.get(), credentialsField);
        if(configResult.isPresent()) {
            return createSourceInfoField(ID, true, configResult.get());
        }

        addArgumentMessages(configResult.argumentMessages());
        return null;
    }
}
