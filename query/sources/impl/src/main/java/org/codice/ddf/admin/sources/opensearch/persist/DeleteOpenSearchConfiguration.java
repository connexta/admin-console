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
package org.codice.ddf.admin.sources.opensearch.persist;

import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.common.services.ServiceCommons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class DeleteOpenSearchConfiguration extends BaseFunctionField<BooleanField> {
    public static final String FIELD_NAME = "deleteOpenSearchSource";

    public static final String DESCRIPTION =
            "Deletes an OpenSearch source configuration specified by the pid.";

    public static final BooleanField RETURN_TYPE = new BooleanField();

    private PidField pid;

    private ServiceCommons serviceCommons;

    private final ConfiguratorSuite configuratorSuite;

    public DeleteOpenSearchConfiguration(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        pid = new PidField();
        pid.isRequired(true);
        updateArgumentPaths();

        serviceCommons = new ServiceCommons(configuratorSuite);
    }

    @Override
    public BooleanField performFunction() {
        addErrorMessages(serviceCommons.deleteService(pid));
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }
        addErrorMessages(serviceCommons.serviceConfigurationExists(pid));
    }

    @Override
    public BooleanField getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new DeleteOpenSearchConfiguration(configuratorSuite);
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(DefaultMessages.FAILED_PERSIST,
                DefaultMessages.NO_EXISTING_CONFIG);
    }
}
