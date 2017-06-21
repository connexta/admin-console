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
package org.codice.ddf.admin.sources.opensearch.discover;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.ReportWithResult;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils;

import com.google.common.collect.ImmutableList;

public class DiscoverOpenSearchSource
        extends BaseFunctionField<OpenSearchSourceConfigurationField> {

    public static final String FIELD_NAME = "discoverOpenSearch";

    public static final String DESCRIPTION =
            "Attempts to discover an OpenSearch source using the given hostname and port or URL. If a URL "
                    + "is provided, it will take precedence over a hostname and port.";

    private CredentialsField credentials;

    private AddressField address;

    private OpenSearchSourceUtils openSearchSourceUtils;

    public DiscoverOpenSearchSource() {
        super(FIELD_NAME, DESCRIPTION, new OpenSearchSourceConfigurationField());
        credentials = new CredentialsField();
        address = new AddressField();
        address.isRequired(true);
        updateArgumentPaths();

        openSearchSourceUtils = new OpenSearchSourceUtils();
    }

    public DiscoverOpenSearchSource(OpenSearchSourceUtils openSearchSourceUtils) {
        this();
        this.openSearchSourceUtils = openSearchSourceUtils;
    }

    @Override
    public OpenSearchSourceConfigurationField performFunction() {
        ReportWithResult<ResponseField> responseField;
        if (address.url() != null) {
            responseField = openSearchSourceUtils.sendRequest(address.urlField(), credentials);
        } else {
            responseField = openSearchSourceUtils.discoverOpenSearchUrl(address.host(),
                    credentials);
        }

        addMessages(responseField);
        if (containsErrorMsgs()) {
            return null;
        }

        ReportWithResultImpl<OpenSearchSourceConfigurationField> configResult =
                openSearchSourceUtils.getOpenSearchConfig(responseField.result(), credentials);

        addMessages(configResult);
        return configResult.isResultPresent() ? configResult.result() : null;
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(credentials, address);
    }

    @Override
    public FunctionField<OpenSearchSourceConfigurationField> newInstance() {
        return new DiscoverOpenSearchSource();
    }
}
