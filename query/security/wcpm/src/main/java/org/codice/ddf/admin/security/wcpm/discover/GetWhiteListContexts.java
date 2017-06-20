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
package org.codice.ddf.admin.security.wcpm.discover;

import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.getWhitelistContexts;

import java.util.List;

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

public class GetWhiteListContexts extends GetFunctionField<ContextPath.ContextPaths> {

    public static final String DEFAULT_FIELD_NAME = "whitelisted";

    public static final String DESCRIPTION =
            "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

    public static final ContextPath.ContextPaths RETURN_TYPE =
            new ContextPath.ContextPaths();

    private final ServiceActions serviceActions;

    public GetWhiteListContexts(ServiceActions serviceActions) {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);

        this.serviceActions = serviceActions;
    }

    @Override
    public ContextPath.ContextPaths performFunction() {
        List<String> whiteListStrs = getWhitelistContexts(serviceActions);
        ContextPath.ContextPaths whiteListedField = new ContextPath.ContextPaths();
        for (String whiteListStr : whiteListStrs) {
            ContextPath newContextPath = new ContextPath();
            newContextPath.setValue(whiteListStr);
            whiteListedField.add(newContextPath);
        }
        return whiteListedField;
    }

    @Override
    public ContextPath.ContextPaths getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public FunctionField<ContextPath.ContextPaths> newInstance() {
        return new GetWhiteListContexts(serviceActions);
    }
}
