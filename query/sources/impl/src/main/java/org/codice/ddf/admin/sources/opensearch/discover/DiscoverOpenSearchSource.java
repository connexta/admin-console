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
package org.codice.ddf.admin.sources.opensearch.discover;

import com.google.common.annotations.VisibleForTesting;
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
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;

public class DiscoverOpenSearchSource
    extends BaseFunctionField<OpenSearchSourceConfigurationField> {

  public static final String FIELD_NAME = "discover";

  public static final String DESCRIPTION =
      "Attempts to discover an OpenSearch source using the given hostname and port or URL. If a URL "
          + "is provided, it will take precedence over a hostname and port.";

  public static final OpenSearchSourceConfigurationField RETURN_TYPE =
      new OpenSearchSourceConfigurationField();

  private CredentialsField credentials;

  private AddressField address;

  private OpenSearchSourceUtils openSearchSourceUtils;

  private final ConfiguratorSuite configuratorSuite;

  public DiscoverOpenSearchSource(ConfiguratorSuite configuratorSuite) {
    super(FIELD_NAME, DESCRIPTION);
    this.configuratorSuite = configuratorSuite;

    credentials = new CredentialsField();
    address = new AddressField();
    address.isRequired(true);

    openSearchSourceUtils = new OpenSearchSourceUtils(configuratorSuite);
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(credentials, address);
  }

  @Override
  public OpenSearchSourceConfigurationField performFunction() {
    Report<OpenSearchSourceConfigurationField> configResult;
    if (address.url() != null) {
      configResult =
          openSearchSourceUtils.getOpenSearchConfigFromUrl(address.urlField(), credentials);
    } else {
      configResult = openSearchSourceUtils.getOpenSearchConfigFromHost(address.host(), credentials);
    }

    addErrorMessages(configResult);
    if (containsErrorMsgs()) {
      return null;
    }

    return configResult.getResult();
  }

  @Override
  public OpenSearchSourceConfigurationField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField<OpenSearchSourceConfigurationField> newInstance() {
    return new DiscoverOpenSearchSource(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(DefaultMessages.CANNOT_CONNECT, DefaultMessages.UNKNOWN_ENDPOINT);
  }

  @VisibleForTesting
  private void setOpenSearchSourceUtils(OpenSearchSourceUtils openSearchSourceUtils) {
    this.openSearchSourceUtils = openSearchSourceUtils;
  }
}
