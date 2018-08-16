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
package org.codice.ddf.admin.sources.csw.discover;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.sources.csw.CswSourceUtils;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;

public class DiscoverCswSource extends BaseFunctionField<CswSourceConfigurationField> {

  public static final String FIELD_NAME = "discover";

  public static final String DESCRIPTION =
      "Attempts to discover a CSW source using the given hostname and port or URL. If a URL is provided, "
          + "it will take precedence over a hostname and port.";

  public static final CswSourceConfigurationField RETURN_TYPE = new CswSourceConfigurationField();

  private CredentialsField credentials;

  private AddressField address;

  private final CswSourceUtils cswSourceUtils;

  public DiscoverCswSource(CswSourceUtils cswSourceUtils) {
    super(FIELD_NAME, DESCRIPTION);
    this.cswSourceUtils = cswSourceUtils;

    credentials = new CredentialsField();
    address = new AddressField();
    address.isRequired(true);
  }

  @Override
  public CswSourceConfigurationField performFunction() {
    Report<CswSourceConfigurationField> configResult;
    if (address.url() != null) {
      configResult = cswSourceUtils.getCswConfigFromUrl(address.urlField(), credentials);
    } else {
      configResult = cswSourceUtils.getConfigFromHost(address.host(), credentials);
    }

    addErrorMessages(configResult);
    if (containsErrorMsgs()) {
      return null;
    }

    return configResult.getResult();
  }

  @Override
  public CswSourceConfigurationField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(credentials, address);
  }

  @Override
  public FunctionField<CswSourceConfigurationField> newInstance() {
    return new DiscoverCswSource(cswSourceUtils);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(DefaultMessages.CANNOT_CONNECT, DefaultMessages.UNKNOWN_ENDPOINT);
  }
}
