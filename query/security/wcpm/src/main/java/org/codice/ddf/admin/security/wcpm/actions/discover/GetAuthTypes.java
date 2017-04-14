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

import java.util.Arrays;

import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.security.common.fields.wcpm.AuthType;

public class GetAuthTypes extends GetAction<ListField<AuthType>> {

    public static final String FIELD_NAME = "authTypes";

    public static final String DESCRIPTION =
            "Retrieves all currently configured authentication types.";

    private Configurator configurator;

    public GetAuthTypes(Configurator configurator) {
        super(FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(AuthType.class));
        this.configurator = configurator;
    }

    @Override
    public ListField<AuthType> performAction() {
        // TODO: 4/3/17 Should implement the different auth type action creators once implemented
        return new ListFieldImpl<>(AuthType.class).addAll(Arrays.asList(AuthType.BASIC_AUTH,
                AuthType.SAML_AUTH,
                AuthType.PKI_AUTH,
                AuthType.GUEST_AUTH,
                AuthType.IDP_AUTH));
    }
}
