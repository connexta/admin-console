/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.security.wcpm.discover;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin;
import org.codice.ddf.admin.security.wcpm.PolicyManagerServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class GetContextPolicies extends GetFunctionField<ContextPolicyBin.ListImpl> {

  public static final String DEFAULT_FIELD_NAME = "policies";

  public static final String DESCRIPTION =
      "Returns all currently configured policies applied to context paths.";

  private ContextPolicyBin.ListImpl returnType;

  private final ServiceReader serviceReader;

  private PolicyManagerServiceProperties wcpmServiceProps = new PolicyManagerServiceProperties();

  public GetContextPolicies(ServiceReader serviceReader) {
    super(DEFAULT_FIELD_NAME, DESCRIPTION);
    this.serviceReader = serviceReader;
    this.returnType = new ContextPolicyBin.ListImpl(serviceReader);
  }

  @Override
  public ContextPolicyBin.ListImpl performFunction() {
    return wcpmServiceProps.contextPolicyServiceToContextPolicyFields(serviceReader);
  }

  @Override
  public ContextPolicyBin.ListImpl getReturnType() {
    return returnType;
  }

  @Override
  public FunctionField<ContextPolicyBin.ListImpl> newInstance() {
    return new GetContextPolicies(serviceReader);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of();
  }
}
