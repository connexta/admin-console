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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.security.common.fields.wcpm.AuthType;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class GetAuthTypes extends GetFunctionField<AuthType.ListImpl> {

  public static final String FIELD_NAME = "authTypes";

  public static final String DESCRIPTION =
      "Retrieves all currently configured authentication types.";

  private AuthType.ListImpl returnType;

  private final ServiceReader serviceReader;

  public GetAuthTypes(ServiceReader serviceReader) {
    super(FIELD_NAME, DESCRIPTION);
    this.serviceReader = serviceReader;
    this.returnType = new AuthType.ListImpl(serviceReader);
  }

  @Override
  public AuthType.ListImpl performFunction() {
    List authType =
        new AuthType(serviceReader)
            .getEnumValues()
            .stream()
            .map(enumVal -> new AuthType(serviceReader, enumVal))
            .collect(Collectors.toList());

    return new AuthType.ListImpl(serviceReader).addAll(authType);
  }

  @Override
  public AuthType.ListImpl getReturnType() {
    return returnType;
  }

  @Override
  public FunctionField<AuthType.ListImpl> newInstance() {
    return new GetAuthTypes(serviceReader);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of();
  }
}
