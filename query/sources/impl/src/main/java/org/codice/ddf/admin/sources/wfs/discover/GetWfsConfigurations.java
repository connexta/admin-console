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

import static org.codice.ddf.admin.sources.services.WfsServiceProperties.SERVICE_PROPS_TO_WFS_CONFIG;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.WFS_FACTORY_PIDS;

import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
import org.codice.ddf.admin.sources.wfs.WfsSourceInfoField;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class GetWfsConfigurations extends BaseFunctionField<ListField<WfsSourceInfoField>> {

    public static final String FIELD_NAME = "sources";

    public static final String DESCRIPTION =
            "Retrieves all currently configured WFS sources. If a source pid is specified, only that source configuration will be returned.";

    public static final WfsSourceInfoField.ListImpl RETURN_TYPE = new WfsSourceInfoField.ListImpl();

    private PidField pid;

    private SourceUtilCommons sourceUtilCommons;

    private ServiceCommons serviceCommons;

    private final ConfiguratorSuite configuratorSuite;

    public GetWfsConfigurations(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        pid = new PidField();
        updateArgumentPaths();

        sourceUtilCommons = new SourceUtilCommons(configuratorSuite);
        serviceCommons = new ServiceCommons(configuratorSuite);
    }

    @Override
    public ListField<WfsSourceInfoField> performFunction() {
        WfsSourceInfoField.ListImpl cswSourceInfoFields = new WfsSourceInfoField.ListImpl();

        List<WfsSourceConfigurationField> configs = sourceUtilCommons.getSourceConfigurations(
                WFS_FACTORY_PIDS,
                SERVICE_PROPS_TO_WFS_CONFIG,
                pid.getValue());

        configs.forEach(config -> cswSourceInfoFields.add(new WfsSourceInfoField().config(config)));

        for (WfsSourceInfoField sourceInfoField : cswSourceInfoFields.getList()) {
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
            addReportMessages(serviceCommons.serviceConfigurationExists(pid));
        }
    }

    @Override
    public List getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<ListField<WfsSourceInfoField>> newInstance() {
        return new GetWfsConfigurations(configuratorSuite);
    }

    @Override
    public ListField<WfsSourceInfoField> getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(DefaultMessages.NO_EXISTING_CONFIG);
    }
}
