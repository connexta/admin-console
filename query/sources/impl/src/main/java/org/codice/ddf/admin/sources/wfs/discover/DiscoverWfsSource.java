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
package org.codice.ddf.admin.sources.wfs.discover;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.admin.sources.wfs.WfsSourceUtils;

import com.google.common.collect.ImmutableList;

public class DiscoverWfsSource extends BaseFunctionField<WfsSourceConfigurationField> {

    public static final String FIELD_NAME = "discoverWfs";

    public static final String DESCRIPTION =
            "Attempts to discover a WFS source using the given hostname and port or URL. If a URL"
                    + " is provided, it will take precedence over a hostname and port.";

    private CredentialsField credentials;

    private AddressField address;

    private WfsSourceUtils wfsSourceUtils;

    public DiscoverWfsSource() {
        super(FIELD_NAME, DESCRIPTION, new WfsSourceConfigurationField());
        credentials = new CredentialsField();
        address = new AddressField();
        address.isRequired(true);
        updateArgumentPaths();

        wfsSourceUtils = new WfsSourceUtils();
    }

    public DiscoverWfsSource(WfsSourceUtils wfsSourceUtils) {
        this();
        this.wfsSourceUtils = wfsSourceUtils;
    }

    @Override
    public WfsSourceConfigurationField performFunction() {
        ReportWithResultImpl<ResponseField> responseResult;
        if (address.url() != null) {
            responseResult = wfsSourceUtils.sendRequest(address.urlField(), credentials);
        } else {
            responseResult = wfsSourceUtils.discoverWfsUrl(address.host(), credentials);
        }

        addMessages(responseResult);
        if (containsErrorMsgs()) {
            return null;
        }

        ReportWithResultImpl<WfsSourceConfigurationField> configResult =
                wfsSourceUtils.getPreferredWfsConfig(responseResult.result(),
                        credentials,
                        address.urlField());

        addMessages(configResult);
        return containsErrorMsgs() ? null : configResult.result();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(credentials, address);
    }

    @Override
    public FunctionField<WfsSourceConfigurationField> newInstance() {
        return new DiscoverWfsSource();
    }
}
