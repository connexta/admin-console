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
package org.codice.ddf.admin.sources.wfs.discover;

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.SERVICE_PROPS_TO_WFS_CONFIG;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.WFS_FACTORY_PIDS;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.type.SourceConfigField;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
import org.codice.ddf.admin.sources.wfs.WfsSourceInfoField;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

import com.google.common.collect.ImmutableList;

public class GetWfsConfigurations extends BaseFunctionField<ListField<WfsSourceInfoField>> {

    public static final String FIELD_NAME = "sources";

    public static final String DESCRIPTION =
            "Retrieves all currently configured WFS sources. If a source pid is specified, only that source configuration will be returned.";

    public static final ListFieldImpl<WfsSourceInfoField> RETURN_TYPE =
            new ListFieldImpl<>(WfsSourceInfoField.class);

    public static final String WFS_SOURCES = "wfsSources";

    private PidField pid;

    private SourceUtilCommons sourceUtilCommons;

    private ServiceCommons serviceCommons;

    private final ConfiguratorFactory configuratorFactory;

    private final ServiceActions serviceActions;

    private final ManagedServiceActions managedServiceActions;

    private final ServiceReader serviceReader;

    public GetWfsConfigurations(ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions, ManagedServiceActions managedServiceActions,
            ServiceReader serviceReader) {
        super(FIELD_NAME, DESCRIPTION);
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
    public ListField<WfsSourceInfoField> performFunction() {
        ListField<WfsSourceInfoField> cswSourceInfoFields = new ListFieldImpl<>(WFS_SOURCES,
                WfsSourceInfoField.class);

        List<SourceConfigField> configs =
                sourceUtilCommons.getSourceConfigurations(WFS_FACTORY_PIDS,
                        SERVICE_PROPS_TO_WFS_CONFIG,
                        pid.getValue());

        configs.forEach(config -> {
            cswSourceInfoFields.add(new WfsSourceInfoField().config((WfsSourceConfigurationField) config));
        });

        for (WfsSourceInfoField sourceInfoField : cswSourceInfoFields.getList()) {
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
    public FunctionField<ListField<WfsSourceInfoField>> newInstance() {
        return new GetWfsConfigurations(configuratorFactory,
                serviceActions,
                managedServiceActions,
                serviceReader);
    }

    @Override
    public ListField<WfsSourceInfoField> getReturnType() {
        return RETURN_TYPE;
    }
}
