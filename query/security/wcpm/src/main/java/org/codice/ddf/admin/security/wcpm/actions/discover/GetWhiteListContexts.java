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
package org.codice.ddf.admin.security.wcpm.actions.discover;

import java.util.List;

import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.common.fields.common.ContextPaths;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.codice.ddf.security.policy.context.impl.PolicyManager;

public class GetWhiteListContexts extends GetAction<ContextPaths> {

    public static final String DEFAULT_FIELD_NAME = "whitelisted";

    public static final String DESCRIPTION =
            "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

    private Configurator configurator;

    public GetWhiteListContexts(Configurator configurator) {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPaths());
        this.configurator = configurator;

    }

    @Override
    public ContextPaths performAction() {
        ContextPolicyManager ref = configurator.getServiceReference(ContextPolicyManager.class);
        PolicyManager policyManager = ((PolicyManager) ref);
        List<String> whiteListStrs = policyManager.getWhiteListContexts();

        ContextPaths whiteListedField = new ContextPaths();
        for (String whiteListStr : whiteListStrs) {
            ContextPath newContextPath = new ContextPath();
            newContextPath.setValue(whiteListStr);
            whiteListedField.add(newContextPath);
        }
        return whiteListedField;
    }
}
