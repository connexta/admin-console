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
package org.codice.ddf.admin.sources.csw.discover;

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.CSW_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.SERVICE_PROPS_TO_CSW_CONFIG;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.csw.CswSourceInfoField;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigField;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

import com.google.common.collect.ImmutableList;

public class GetCswConfigurations extends BaseFunctionField<ListField<CswSourceInfoField>> {

    public static final String FIELD_NAME = "sources";

    public static final String DESCRIPTION =
            "Retrieves all currently configured CSW sources. If the pid argument is specified, only the source configuration with that pid will be returned.";

    public static final String CSW_SOURCES = "cswSources";

    private PidField pid;

    private SourceUtilCommons sourceUtilCommons;

    private ServiceCommons serviceCommons;

    private final ConfiguratorFactory configuratorFactory;

    private final ServiceActions serviceActions;

    private final ManagedServiceActions managedServiceActions;

    private final ServiceReader serviceReader;

    public GetCswConfigurations(ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions, ManagedServiceActions managedServiceActions,
            ServiceReader serviceReader) {
        super(FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(CswSourceInfoField.class));
        this.configuratorFactory = configuratorFactory;
        this.serviceActions = serviceActions;
        this.managedServiceActions = managedServiceActions;
        this.serviceReader = serviceReader;

        pid = new PidField();
        updateArgumentPaths();

        sourceUtilCommons = new SourceUtilCommons(managedServiceActions,
                serviceActions,
                serviceReader,
                configuratorFactory);

        serviceCommons = new ServiceCommons(null, serviceActions, null, null);
    }

    @Override
    public ListField<CswSourceInfoField> performFunction() {
        ListField<CswSourceInfoField> cswSourceInfoFields = new ListFieldImpl<>(CSW_SOURCES,
                CswSourceInfoField.class);

        List<SourceConfigField> configs =
                sourceUtilCommons.getSourceConfigurations(CSW_FACTORY_PIDS,
                        SERVICE_PROPS_TO_CSW_CONFIG,
                        pid.getValue());

        configs.forEach(config -> {
            cswSourceInfoFields.add(new CswSourceInfoField().config((CswSourceConfigurationField) config));
        });

        for (CswSourceInfoField sourceInfoField : cswSourceInfoFields.getList()) {
            sourceUtilCommons.populateAvailability(sourceInfoField.isAvailableField(),
                    sourceInfoField.config()
                            .pidField());
            sourceInfoField.config()
                    .credentials()
                    .password(FLAG_PASSWORD);
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
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<ListField<CswSourceInfoField>> newInstance() {
        return new GetCswConfigurations(configuratorFactory,
                serviceActions,
                managedServiceActions,
                serviceReader);
    }
}
