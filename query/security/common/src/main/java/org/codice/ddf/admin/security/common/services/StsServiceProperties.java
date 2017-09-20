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
package org.codice.ddf.admin.security.common.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

public class StsServiceProperties {

  // Sts Service Props
  public static final String STS_CLAIMS_CONFIGURATION_CONFIG_ID =
      "ddf.security.sts.client.configuration";

  public static final String STS_CLAIMS_PROPS_KEY_CLAIMS = "claims";
  //

  public List<String> getConfiguredStsClaims(ServiceActions serviceActions) {
    Map<String, Object> stsConfig = serviceActions.read(STS_CLAIMS_CONFIGURATION_CONFIG_ID);

    return stsConfig != null
        ? Arrays.asList((String[]) stsConfig.get(STS_CLAIMS_PROPS_KEY_CLAIMS))
        : new ArrayList<>();
  }
}
