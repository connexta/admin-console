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

import org.codice.ddf.admin.api.ConfiguratorSuite;
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

    public static final OpenSearchSourceConfigurationField RETURN_TYPE = new OpenSearchSourceConfigurationField();

    private CredentialsField credentials;

    private AddressField address;

    private OpenSearchSourceUtils openSearchSourceUtils;

    private final ConfiguratorSuite configuratorSuite;

    public DiscoverOpenSearchSource(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        credentials = new CredentialsField();
        address = new AddressField();
        address.isRequired(true);
        updateArgumentPaths();

        openSearchSourceUtils = new OpenSearchSourceUtils(configuratorSuite);
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(credentials, address);
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
    public OpenSearchSourceConfigurationField getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public FunctionField<OpenSearchSourceConfigurationField> newInstance() {
        return new DiscoverOpenSearchSource(configuratorSuite);
    }

    /**
     * For testing purposes only. Groovy can access private methods
     */
    private void setOpenSearchSourceUtils(OpenSearchSourceUtils openSearchSourceUtils) {
        this.openSearchSourceUtils = openSearchSourceUtils;
    }
}
