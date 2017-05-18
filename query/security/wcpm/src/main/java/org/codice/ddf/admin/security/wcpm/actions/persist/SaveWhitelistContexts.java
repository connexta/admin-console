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
package org.codice.ddf.admin.security.wcpm.actions.persist;

import static org.codice.ddf.admin.common.message.DefaultMessages.failedPersistError;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties;
import org.codice.ddf.internal.admin.configurator.opfactory.AdminOpFactory;

import com.google.common.collect.ImmutableList;

public class SaveWhitelistContexts extends BaseAction<ListField<ContextPath>> {

    public static final String ACTION_ID = "saveWhitelistContexts";

    public static final String DESCRIPTION =
            "Persists the given contexts paths as white listed contexts. White listing a context path will result in no security being applied to the given paths.";

    private ListField<ContextPath> contexts;

    private ConfiguratorFactory configuratorFactory;

    private AdminOpFactory adminOpFactory;

    public SaveWhitelistContexts(ConfiguratorFactory configuratorFactory,
            AdminOpFactory adminOpFactory) {
        super(ACTION_ID, DESCRIPTION, new ListFieldImpl<>(ContextPath.class));
        contexts = new ListFieldImpl<>("paths", new ContextPath());

        this.configuratorFactory = configuratorFactory;
        this.adminOpFactory = adminOpFactory;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(contexts);
    }

    @Override
    public ListField<ContextPath> performAction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.add(adminOpFactory.build(PolicyManagerServiceProperties.POLICY_MANAGER_PID,
                new PolicyManagerServiceProperties().whiteListToPolicyManagerProps(contexts),
                true));

        OperationReport configReport = configurator.commit(
                "Whitelist Contexts saved with details: {}",
                contexts.toString());

        if(configReport.containsFailedResults()) {
            addMessage(failedPersistError());
        }

        return configReport.containsFailedResults() ? null : contexts;
    }
}
