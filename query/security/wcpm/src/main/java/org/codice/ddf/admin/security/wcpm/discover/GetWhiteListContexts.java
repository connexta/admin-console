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
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;

public class GetWhiteListContexts extends GetFunctionField<ListField<ContextPath>> {

    public static final String DEFAULT_FIELD_NAME = "whitelisted";

    public static final String DESCRIPTION =
            "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

    private ConfiguratorFactory configuratorFactory;

    public GetWhiteListContexts(ConfiguratorFactory configuratorFactory) {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(ContextPath.class));
        this.configuratorFactory = configuratorFactory;

    }

    @Override
    public ListField<ContextPath> performFunction() {
        List<String> whiteListStrs = getWhitelistContexts(configuratorFactory.getConfigReader());
        ListField<ContextPath> whiteListedField = new ListFieldImpl<>(ContextPath.class);
        for (String whiteListStr : whiteListStrs) {
            ContextPath newContextPath = new ContextPath();
            newContextPath.setValue(whiteListStr);
            whiteListedField.add(newContextPath);
        }
        return whiteListedField;
    }

    @Override
    public FunctionField<ListField<ContextPath>> newInstance() {
        return new GetWhiteListContexts(configuratorFactory);
    }
}
