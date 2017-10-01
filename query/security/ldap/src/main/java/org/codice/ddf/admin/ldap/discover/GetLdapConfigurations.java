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
package org.codice.ddf.admin.ldap.discover;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.ldap.commons.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;

public class GetLdapConfigurations extends GetFunctionField<ListField<LdapConfigurationField>> {

  public static final String FIELD_NAME = "configs";

  public static final String DESCRIPTION = "Retrieves all currently configured LDAP settings.";

  public static final LdapConfigurationField.ListImpl RETURN_TYPE =
      new LdapConfigurationField.ListImpl();

  private final ConfiguratorSuite configuratorSuite;

  private LdapServiceCommons serviceCommons;

  public GetLdapConfigurations(ConfiguratorSuite configuratorSuite) {
    super(FIELD_NAME, DESCRIPTION);
    this.configuratorSuite = configuratorSuite;

    serviceCommons = new LdapServiceCommons(configuratorSuite);
  }

  @Override
  public ListField<LdapConfigurationField> performFunction() {
    return serviceCommons.getLdapConfigurations();
  }

  @Override
  public ListField<LdapConfigurationField> getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField<ListField<LdapConfigurationField>> newInstance() {
    return new GetLdapConfigurations(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of();
  }
}
