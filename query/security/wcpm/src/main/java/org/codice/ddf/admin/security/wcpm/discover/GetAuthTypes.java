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

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.security.common.fields.wcpm.AuthType;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class GetAuthTypes extends GetFunctionField<AuthType.AuthTypes> {

    public static final String FIELD_NAME = "authTypes";

    public static final String DESCRIPTION =
            "Retrieves all currently configured authentication types.";

    private AuthType.AuthTypes returnType;

    private ConfiguratorFactory configuratorFactory;
    private ServiceReader serviceReader;

    public GetAuthTypes(ConfiguratorFactory configuratorFactory, ServiceReader serviceReader) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorFactory = configuratorFactory;
        this.serviceReader = serviceReader;
        this.returnType = new AuthType.AuthTypes(serviceReader);
    }

    @Override
    public AuthType.AuthTypes performFunction() {
        return new AuthType.AuthTypes(serviceReader);
    }

    @Override
    public AuthType.AuthTypes getReturnType() {
        return returnType;
    }

    @Override
    public FunctionField<AuthType.AuthTypes> newInstance() {
        return new GetAuthTypes(configuratorFactory, serviceReader);
    }
}
