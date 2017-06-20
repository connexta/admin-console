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

import java.util.Arrays;

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.security.common.fields.wcpm.AuthType;

public class GetAuthTypes extends GetFunctionField<AuthType.AuthTypes> {

    public static final String FIELD_NAME = "authTypes";

    public static final String DESCRIPTION =
            "Retrieves all currently configured authentication types.";

    public static final AuthType.AuthTypes RETURN_TYPE = new AuthType.AuthTypes();

    private ConfiguratorFactory configuratorFactory;

    public GetAuthTypes(ConfiguratorFactory configuratorFactory) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public AuthType.AuthTypes performFunction() {
        return new AuthType.AuthTypes().addAll(Arrays.asList(AuthType.BASIC_AUTH,
                AuthType.SAML_AUTH,
                AuthType.PKI_AUTH,
                AuthType.GUEST_AUTH,
                AuthType.IDP_AUTH));
    }

    @Override
    public AuthType.AuthTypes getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public FunctionField<AuthType.AuthTypes> newInstance() {
        return new GetAuthTypes(configuratorFactory);
    }
}
