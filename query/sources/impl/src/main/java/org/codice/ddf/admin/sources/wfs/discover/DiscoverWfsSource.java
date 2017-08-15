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
import java.util.Set;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.admin.sources.wfs.WfsSourceUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class DiscoverWfsSource extends BaseFunctionField<WfsSourceConfigurationField> {

    public static final String FIELD_NAME = "discover";

    public static final String DESCRIPTION =
            "Attempts to discover a WFS source using the given hostname and port or URL. If a URL"
                    + " is provided, it will take precedence over a hostname and port.";

    public static final WfsSourceConfigurationField RETURN_TYPE = new WfsSourceConfigurationField();

    private CredentialsField credentials;

    private AddressField address;

    private WfsSourceUtils wfsSourceUtils;

    private final ConfiguratorSuite configuratorSuite;

    public DiscoverWfsSource(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        credentials = new CredentialsField();
        address = new AddressField();
        address.isRequired(true);
        updateArgumentPaths();

        wfsSourceUtils = new WfsSourceUtils(configuratorSuite);
    }

    @Override
    public WfsSourceConfigurationField performFunction() {
        Report<WfsSourceConfigurationField> configResult;
        if (address.url() != null) {
            configResult = wfsSourceUtils.getWfsConfigFromUrl(address.urlField(), credentials);
        } else {
            configResult = wfsSourceUtils.getWfsConfigFromHost(address.host(), credentials);
        }

        addErrorMessages(configResult);
        if (containsErrorMsgs()) {
            return null;
        }

        return configResult.getResult();
    }

    @Override
    public WfsSourceConfigurationField getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(credentials, address);
    }

    @Override
    public FunctionField<WfsSourceConfigurationField> newInstance() {
        return new DiscoverWfsSource(configuratorSuite);
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(DefaultMessages.CANNOT_CONNECT,
                DefaultMessages.UNKNOWN_ENDPOINT);
    }

    /**
     * For testing purposes only. Groovy can access private methods
     */
    private void setWfsSourceUtils(WfsSourceUtils wfsSourceUtils) {
        this.wfsSourceUtils = wfsSourceUtils;
    }
}
