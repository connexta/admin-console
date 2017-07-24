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

import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.ReportWithResult;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.sources.csw.CswSourceUtils;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class DiscoverCswSource extends BaseFunctionField<CswSourceConfigurationField> {

    public static final String FIELD_NAME = "discoverCsw";

    public static final String DESCRIPTION =
            "Attempts to discover a CSW source using the given hostname and port or URL. If a URL is provided, "
                    + "it will take precedence over a hostname and port.";

    public static final CswSourceConfigurationField RETURN_TYPE = new CswSourceConfigurationField();

    private CredentialsField credentials;


    private AddressField address;

    private CswSourceUtils cswSourceUtils;

    private final ConfiguratorSuite configuratorSuite;

    public DiscoverCswSource(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        credentials = new CredentialsField();
        address = new AddressField();
        address.isRequired(true);
        updateArgumentPaths();

        cswSourceUtils = new CswSourceUtils(configuratorSuite);
    }

    @Override
    public CswSourceConfigurationField performFunction() {
        ReportWithResult<ResponseField> responseResult;
        if (address.url() != null) {
            responseResult = cswSourceUtils.sendRequest(address.urlField(), credentials);
        } else {
            responseResult = cswSourceUtils.discoverCswUrl(address.host(), credentials);
        }

        addMessages(responseResult);
        if (containsErrorMsgs()) {
            return null;
        }

        ReportWithResult<CswSourceConfigurationField> configResult =
                cswSourceUtils.getPreferredCswConfig(responseResult.result(), credentials);

        addMessages(configResult);
        return configResult.isResultPresent() ? configResult.result() : null;
    }

    @Override
    public CswSourceConfigurationField getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(credentials, address);
    }

    @Override
    public FunctionField<CswSourceConfigurationField> newInstance() {
        return new DiscoverCswSource(configuratorSuite);
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(DefaultMessages.CANNOT_CONNECT,
                DefaultMessages.UNKNOWN_ENDPOINT);
    }

    /**
     * For testing purposes only. Groovy can access private methods
     */
    private void setCswSourceUtils(CswSourceUtils cswSourceUtils) {
        this.cswSourceUtils = cswSourceUtils;
    }
}
