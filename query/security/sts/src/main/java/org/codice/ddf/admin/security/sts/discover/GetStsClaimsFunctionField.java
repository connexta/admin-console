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
package org.codice.ddf.admin.security.sts.discover;

import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.security.common.fields.sts.StsClaimField;
import org.codice.ddf.admin.security.common.services.StsServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableSet;

public class GetStsClaimsFunctionField extends GetFunctionField<ListField<StsClaimField>> {

    public static final String NAME = "claims";

    public static final String DESCRIPTION = "All currently configured claims the STS supports.";

    public static final StsClaimField.ListImpl RETURN_TYPE = new StsClaimField.ListImpl();

    private final ServiceActions serviceActions;

    public GetStsClaimsFunctionField(ServiceActions serviceActions) {
        super(NAME, DESCRIPTION);
        this.serviceActions = serviceActions;
    }

    @Override
    public ListField<StsClaimField> performFunction() {
        List<String> supportedClaims = new StsServiceProperties().getConfiguredStsClaims(
                serviceActions);

        ListField<StsClaimField> claims = new StsClaimField.ListImpl();

        supportedClaims
                .forEach(claim -> {
                    StsClaimField claimField = new StsClaimField();
                    claimField.setValue(claim);
                    claims.add(claimField);
                });

        return claims;
    }

    @Override
    public ListField<StsClaimField> getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public FunctionField<ListField<StsClaimField>> newInstance() {
        return new GetStsClaimsFunctionField(serviceActions);
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of();
    }
}
