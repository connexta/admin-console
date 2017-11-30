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

import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.getWhitelistContexts;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;

public class GetWhiteListContexts extends GetFunctionField<ContextPath.ListImpl> {

  public static final String DEFAULT_FIELD_NAME = "whitelisted";

  public static final String DESCRIPTION =
      "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

  public static final ContextPath.ListImpl RETURN_TYPE = new ContextPath.ListImpl();

  private final ConfiguratorSuite configuratorSuite;

  public GetWhiteListContexts(ConfiguratorSuite configuratorSuite) {
    super(DEFAULT_FIELD_NAME, DESCRIPTION);

    this.configuratorSuite = configuratorSuite;
  }

  @Override
  public ContextPath.ListImpl performFunction() {
    List<String> whiteListStrs = getWhitelistContexts(configuratorSuite);
    ContextPath.ListImpl whiteListedField = new ContextPath.ListImpl();
    for (String whiteListStr : whiteListStrs) {
      ContextPath newContextPath = new ContextPath();
      newContextPath.setValue(whiteListStr);
      whiteListedField.add(newContextPath);
    }
    return whiteListedField;
  }

  @Override
  public ContextPath.ListImpl getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField<ContextPath.ListImpl> newInstance() {
    return new GetWhiteListContexts(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of();
  }
}
