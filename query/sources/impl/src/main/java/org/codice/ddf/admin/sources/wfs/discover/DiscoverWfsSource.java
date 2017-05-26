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

import static org.codice.ddf.admin.sources.commons.utils.SourceUtilCommons.createSourceInfoField;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.admin.sources.commons.utils.WfsSourceUtils;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

import com.google.common.collect.ImmutableList;

public class DiscoverWfsSource extends BaseFunctionField<SourceInfoField> {

    public static final String ID = "discoverWfs";

    public static final String DESCRIPTION =
            "Attempts to discover a WFS source using the given hostname and port or URL. If a URL"
                    + " is provided, it will take precedence over a hostname and port.";

    private CredentialsField credentials;

    private AddressField address;

    private WfsSourceUtils wfsSourceUtils;

    public DiscoverWfsSource() {
        super(ID, DESCRIPTION, new SourceInfoField());
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
    public SourceInfoField performFunction() {
        ReportWithResultImpl<SourceConfigUnionField> configResult;
        if (address.url() != null) {
            configResult = wfsSourceUtils.getPreferredWfsConfig(address.urlField(), credentials);
            addMessages(configResult);
            if (containsErrorMsgs()) {
                return null;
            }
        } else {
            ReportWithResultImpl<UrlField> discoveredUrl = wfsSourceUtils.discoverWfsUrl(address.host(), credentials);
            addMessages(discoveredUrl);
            if (containsErrorMsgs()) {
                return null;
            }

            configResult = wfsSourceUtils.getPreferredWfsConfig(discoveredUrl.result(), credentials);
            addMessages(configResult);
            if (containsErrorMsgs()) {
                return null;
            }
        }
        return createSourceInfoField(true, configResult.result());
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(credentials, address);
    }

    @Override
    public FunctionField<SourceInfoField> newInstance() {
        return  new DiscoverWfsSource();
    }
}
