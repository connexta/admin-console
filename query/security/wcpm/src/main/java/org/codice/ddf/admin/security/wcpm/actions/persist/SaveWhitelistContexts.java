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
import static org.codice.ddf.admin.security.wcpm.commons.ContextPolicyServiceProperties.POLICY_MANAGER_PID;
import static org.codice.ddf.admin.security.wcpm.commons.ContextPolicyServiceProperties.whiteListToPolicyManagerProps;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.common.fields.common.ContextPaths;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.codice.ddf.security.policy.context.impl.PolicyManager;

import com.google.common.collect.ImmutableList;

public class SaveWhitelistContexts extends BaseAction<ContextPaths> {

    public static final String DEFAULT_FIELD_NAME = "saveWhitelistContexts";

    public static final String DESCRIPTION =
            "Persists the given contexts paths as white listed contexts. White listing a context path will result in no security being applied to the given paths.";

    private ContextPaths contexts;

    private Configurator configurator;

    public SaveWhitelistContexts(Configurator configurator) {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPaths());
        contexts = new ContextPaths();
        contexts.isRequired(true);
        this.configurator = configurator;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(contexts);
    }

    @Override
    public ContextPaths performAction() {
        ContextPolicyManager ref = configurator.getServiceReference(ContextPolicyManager.class);
        PolicyManager policyManager = ((PolicyManager) ref);
        ContextPaths preUpdateWhitelistContexts = new ContextPaths();

        for (String path : policyManager.getWhiteListContexts()) {
            preUpdateWhitelistContexts.add(new ContextPath(path));
        }

        configurator.updateConfigFile(POLICY_MANAGER_PID,
                whiteListToPolicyManagerProps(contexts),
                true);

        OperationReport configReport = configurator.commit(
                "Whitelist Contexts saved with details: {}",
                contexts.toString());

        if (configReport.containsFailedResults()) {
            addArgumentMessage(failedPersistError(name()));
            return preUpdateWhitelistContexts;
        } else {
            return contexts;
        }
    }
}
