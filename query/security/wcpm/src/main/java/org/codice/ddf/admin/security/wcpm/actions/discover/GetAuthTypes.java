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

import static org.codice.ddf.admin.security.common.fields.wcpm.services.PolicyManagerServiceProperties.IDP_CLIENT_BUNDLE_NAME;

import java.util.Arrays;

import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.security.common.fields.wcpm.AuthType;

public class GetAuthTypes extends GetAction<ListField<AuthType>> {

    public static final String FIELD_NAME = "authTypes";

    public static final String DESCRIPTION =
            "Retrieves all currently configured authentication types.";

    private ConfiguratorFactory configuratorFactory;

    public GetAuthTypes(ConfiguratorFactory configuratorFactory) {
        super(FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(AuthType.class));
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public ListField<AuthType> performAction() {
        // TODO: tbatie - 4/14/17 - Check the backend to see if these services are running
        ListField<AuthType> authTypes = new ListFieldImpl<>(AuthType.class).addAll(Arrays.asList(AuthType.BASIC_AUTH,
                AuthType.SAML_AUTH,
                AuthType.PKI_AUTH,
                AuthType.GUEST_AUTH));

        if (configuratorFactory.getConfigReader().isBundleStarted(IDP_CLIENT_BUNDLE_NAME)) {
            authTypes.add(AuthType.IDP_AUTH);
        }

        return authTypes;
    }
}
