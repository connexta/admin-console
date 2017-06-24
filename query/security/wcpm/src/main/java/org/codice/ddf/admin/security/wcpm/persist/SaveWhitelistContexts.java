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
package org.codice.ddf.admin.security.wcpm.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableList;

public class SaveWhitelistContexts extends BaseFunctionField<ContextPath.ListImpl> {

    public static final String FIELD_NAME = "saveWhitelistContexts";

    public static final String DESCRIPTION =
            "Persists the given contexts paths as white listed contexts. White listing a context path will result in no security being applied to the given paths.";

    public static final ContextPath.ListImpl RETURN_TYPE =
            new ContextPath.ListImpl();

    private ContextPath.ListImpl contexts;

    private ConfiguratorFactory configuratorFactory;

    private final ServiceActions serviceActions;

    public SaveWhitelistContexts(ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorFactory = configuratorFactory;
        this.serviceActions = serviceActions;

        contexts = new ContextPath.ListImpl();
        updateArgumentPaths();
    }

    @Override
    public ContextPath.ListImpl performFunction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.add(serviceActions.build(PolicyManagerServiceProperties.POLICY_MANAGER_PID,
                new PolicyManagerServiceProperties().whiteListToPolicyManagerProps(contexts),
                true));

        OperationReport configReport = configurator.commit(
                "Whitelist Contexts saved with details: {}",
                contexts.toString());

        if (configReport.containsFailedResults()) {
            addResultMessage(failedPersistError());
        }

        return configReport.containsFailedResults() ? null : contexts;
    }

    @Override
    public ContextPath.ListImpl getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(contexts);
    }

    @Override
    public SaveWhitelistContexts newInstance() {
        return new SaveWhitelistContexts(configuratorFactory, serviceActions);
    }
}
