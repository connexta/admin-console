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

import static org.codice.ddf.admin.sources.services.CswServiceProperties.CSW_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.SERVICE_PROPS_TO_CSW_CONFIG;

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
import org.codice.ddf.admin.sources.csw.CswSourceInfoField;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class GetCswConfigurations extends BaseFunctionField<ListField<CswSourceInfoField>> {

    public static final String FIELD_NAME = "sources";

    public static final String DESCRIPTION =
            "Retrieves all currently configured CSW sources. If the pid argument is specified, only the source configuration with that pid will be returned.";

    public static final ListField<CswSourceInfoField> RETURN_TYPE =
            new CswSourceInfoField.ListImpl();

    private PidField pid;

    private SourceUtilCommons sourceUtilCommons;

    private ServiceCommons serviceCommons;

    private final ConfiguratorSuite configuratorSuite;

    public GetCswConfigurations(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        pid = new PidField();
        updateArgumentPaths();

        sourceUtilCommons = new SourceUtilCommons(configuratorSuite);

        serviceCommons = new ServiceCommons(configuratorSuite);
    }

    @Override
    public ListField<CswSourceInfoField> performFunction() {
        ListField<CswSourceInfoField> cswSourceInfoFields = new CswSourceInfoField.ListImpl();

        List<CswSourceConfigurationField> configs = sourceUtilCommons.getSourceConfigurations(
                CSW_FACTORY_PIDS,
                SERVICE_PROPS_TO_CSW_CONFIG,
                pid.getValue());

        configs.forEach(config -> cswSourceInfoFields.add(new CswSourceInfoField().config(config)));

        for (CswSourceInfoField sourceInfoField : cswSourceInfoFields.getList()) {
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
            addErrorMessages(serviceCommons.serviceConfigurationExists(pid));
        }
    }

    @Override
    public ListField<CswSourceInfoField> getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<ListField<CswSourceInfoField>> newInstance() {
        return new GetCswConfigurations(configuratorSuite);
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(DefaultMessages.NO_EXISTING_CONFIG);
    }
}
