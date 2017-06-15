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

import static org.codice.ddf.admin.common.services.ServiceCommons.validateServiceConfigurationExists;
import static org.codice.ddf.admin.sources.commons.utils.SourceUtilCommons.getSourceConfigurations;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.SERVICE_PROPS_TO_OPENSEARCH_CONFIG;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

import com.google.common.collect.ImmutableList;

public class GetOpenSearchConfigurations extends BaseFunctionField<ListField<SourceInfoField>> {

    public static final String ID = "openSearchConfigs";

    public static final String DESCRIPTION =
            "Retrieves all currently configured OpenSearch sources. If a source pid is specified, only that source configuration will be returned.";

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    private final ServiceActions serviceActions;

    private final ManagedServiceActions managedServiceActions;

    private final ServiceReader serviceReader;

    public GetOpenSearchConfigurations(ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions, ManagedServiceActions managedServiceActions,
            ServiceReader serviceReader) {
        super(ID, DESCRIPTION, new ListFieldImpl<>(SourceInfoField.class));
        this.configuratorFactory = configuratorFactory;
        this.serviceActions = serviceActions;
        this.managedServiceActions = managedServiceActions;
        this.serviceReader = serviceReader;

        pid = new PidField();
        updateArgumentPaths();
    }

    @Override
    public ListField<SourceInfoField> performFunction() {
        return getSourceConfigurations(OPENSEARCH_FACTORY_PIDS,
                SERVICE_PROPS_TO_OPENSEARCH_CONFIG,
                pid.getValue(),
                serviceActions,
                managedServiceActions,
                serviceReader);
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (pid.getValue() != null) {
            addMessages(validateServiceConfigurationExists(pid, serviceActions));
        }
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<ListField<SourceInfoField>> newInstance() {
        return new GetOpenSearchConfigurations(configuratorFactory,
                serviceActions,
                managedServiceActions,
                serviceReader);
    }
}
