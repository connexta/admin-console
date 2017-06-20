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
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin;
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class GetContextPolicies extends GetFunctionField<ContextPolicyBin.ContextPolicies> {

    public static final String DEFAULT_FIELD_NAME = "policies";

    public static final String DESCRIPTION =
            "Returns all currently configured policies applied to context paths.";

    public static final ContextPolicyBin.ContextPolicies RETURN_TYPE =
            new ContextPolicyBin.ContextPolicies();

    private final ServiceReader serviceReader;

    private PolicyManagerServiceProperties wcpmServiceProps = new PolicyManagerServiceProperties();

    public GetContextPolicies(ServiceReader serviceReader) {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
        this.serviceReader = serviceReader;
    }

    @Override
    public ContextPolicyBin.ContextPolicies performFunction() {
        return wcpmServiceProps.contextPolicyServiceToContextPolicyFields(serviceReader);
    }

    @Override
    public ContextPolicyBin.ContextPolicies getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public FunctionField<ContextPolicyBin.ContextPolicies> newInstance() {
        return new GetContextPolicies(serviceReader);
    }
}
