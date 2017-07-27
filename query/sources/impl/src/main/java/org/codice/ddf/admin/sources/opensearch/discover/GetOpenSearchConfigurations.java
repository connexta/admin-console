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

import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.SERVICE_PROPS_TO_OPENSEARCH_CONFIG;

import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceInfoField;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class GetOpenSearchConfigurations
        extends BaseFunctionField<ListField<OpenSearchSourceInfoField>> {

    public static final String FIELD_NAME = "sources";

    public static final String DESCRIPTION =
            "Retrieves all currently configured OpenSearch sources. If a source pid is specified, only that source configuration will be returned.";

    public static final ListField<OpenSearchSourceInfoField> RETURN_TYPE =
            new OpenSearchSourceInfoField.ListImpl();

    private PidField pid;

    private SourceUtilCommons sourceUtilCommons;

    private ServiceCommons serviceCommons;

    private final ConfiguratorSuite configuratorSuite;

    public GetOpenSearchConfigurations(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        pid = new PidField();
        updateArgumentPaths();

        sourceUtilCommons = new SourceUtilCommons(configuratorSuite);
        serviceCommons = new ServiceCommons(configuratorSuite);

    }

    @Override
    public ListField<OpenSearchSourceInfoField> performFunction() {
        ListField<OpenSearchSourceInfoField> cswSourceInfoFields =
                new OpenSearchSourceInfoField.ListImpl();

        List<OpenSearchSourceConfigurationField> configs =
                sourceUtilCommons.getSourceConfigurations(OPENSEARCH_FACTORY_PIDS,
                        SERVICE_PROPS_TO_OPENSEARCH_CONFIG,
                        pid.getValue());

        configs.forEach(config -> cswSourceInfoFields.add(new OpenSearchSourceInfoField().config(
                config)));

        for (OpenSearchSourceInfoField sourceInfoField : cswSourceInfoFields.getList()) {
            sourceUtilCommons.populateAvailability(sourceInfoField.isAvailableField(),
                    sourceInfoField.config()
                            .pidField());
        }

        return cswSourceInfoFields;
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (pid.getValue() != null) {
            addMessages(serviceCommons.serviceConfigurationExists(pid));
        }
    }

    @Override
    public ListField<OpenSearchSourceInfoField> getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<ListField<OpenSearchSourceInfoField>> newInstance() {
        return new GetOpenSearchConfigurations(configuratorSuite);
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(DefaultMessages.NO_EXISTING_CONFIG);
    }
}
