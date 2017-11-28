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
package org.codice.ddf.admin.sources.opensearch.persist;

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.openSearchConfigToServiceProps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.sources.SourceMessages;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.SourceValidationUtils;

public class UpdateOpenSearchConfiguration extends BaseFunctionField<BooleanField> {

  public static final String FIELD_NAME = "updateOpenSearchSource";

  public static final String DESCRIPTION =
      String.format(
          "Updates a OpenSearch source configuration specified by the pid. A password of {%s} acts as a flag to not update the password.",
          FLAG_PASSWORD);

  public static final BooleanField RETURN_TYPE = new BooleanField();

  private OpenSearchSourceConfigurationField config;

  private SourceValidationUtils sourceValidationUtils;

  private ServiceCommons serviceCommons;

  private final ConfiguratorSuite configuratorSuite;

  public UpdateOpenSearchConfiguration(ConfiguratorSuite configuratorSuite) {
    super(FIELD_NAME, DESCRIPTION);
    this.configuratorSuite = configuratorSuite;

    config = new OpenSearchSourceConfigurationField();
    config.useDefaultRequired();
    config.pidField().isRequired(true);

    sourceValidationUtils = new SourceValidationUtils(configuratorSuite);
    serviceCommons = new ServiceCommons(configuratorSuite);
  }

  @Override
  public BooleanField performFunction() {
    // TODO: 8/23/17 phuffer - Uncommnt once feature starts correctly
    //        Configurator configurator = configuratorSuite.getConfiguratorFactory()
    //                .getConfigurator();
    //        configurator.add(configuratorSuite.getFeatureActions()
    //                .start(OPENSEARCH_FEATURE));
    //        OperationReport report = configurator.commit("Starting feature [{}]",
    // OPENSEARCH_FEATURE);
    //
    //        if (report.containsFailedResults()) {
    //            addErrorMessage(failedPersistError());
    //            return new BooleanField(false);
    //        }

    addErrorMessages(
        serviceCommons.updateService(config.pidField(), openSearchConfigToServiceProps(config)));
    return new BooleanField(!containsErrorMsgs());
  }

  @Override
  public void validate() {
    super.validate();
    if (containsErrorMsgs()) {
      return;
    }
    addErrorMessages(
        sourceValidationUtils.duplicateSourceNameExists(
            config.sourceNameField(), config.pidField()));
  }

  @Override
  public BooleanField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(config);
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new UpdateOpenSearchConfiguration(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(
        DefaultMessages.FAILED_PERSIST,
        DefaultMessages.NO_EXISTING_CONFIG,
        SourceMessages.DUPLICATE_SOURCE_NAME);
  }
}
