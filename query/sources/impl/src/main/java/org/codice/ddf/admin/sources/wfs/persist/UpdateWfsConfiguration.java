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
package org.codice.ddf.admin.sources.wfs.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.WFS1_FEATURE;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.WFS2_FEATURE;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.wfsConfigToServiceProps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.sources.SourceMessages;
import org.codice.ddf.admin.sources.fields.WfsVersion;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.SourceValidationUtils;

public class UpdateWfsConfiguration extends BaseFunctionField<BooleanField> {

  public static final String FIELD_NAME = "updateWfsSource";

  private static final String DESCRIPTION =
      String.format(
          "Updates a WFS source configuration specified by the pid. A password of {%s} acts as a flag to not update the password.",
          FLAG_PASSWORD);

  public static final BooleanField RETURN_TYPE = new BooleanField();

  private WfsSourceConfigurationField config;

  private SourceValidationUtils sourceValidationUtils;

  private ServiceCommons serviceCommons;

  private final ConfiguratorSuite configuratorSuite;

  public UpdateWfsConfiguration(ConfiguratorSuite configuratorSuite) {
    super(FIELD_NAME, DESCRIPTION);
    this.configuratorSuite = configuratorSuite;

    config = new WfsSourceConfigurationField();
    config.useDefaultRequired();
    config.pidField().isRequired(true);
    updateArgumentPaths();

    sourceValidationUtils = new SourceValidationUtils(configuratorSuite);
    serviceCommons = new ServiceCommons(configuratorSuite);
  }

  @Override
  public BooleanField performFunction() {
    Configurator configurator = configuratorSuite.getConfiguratorFactory().getConfigurator();
    OperationReport report = null;
    if (config.wfsVersion().equals(WfsVersion.Wfs2.WFS_VERSION_2)) {
      configurator.add(configuratorSuite.getFeatureActions().start(WFS2_FEATURE));
      report = configurator.commit("Starting feature [{}].", WFS2_FEATURE);
    } else if (config.wfsVersion().equals(WfsVersion.Wfs1.WFS_VERSION_1)) {
      configurator.add(configuratorSuite.getFeatureActions().start(WFS1_FEATURE));
      report = configurator.commit("Starting feature [{}].", WFS1_FEATURE);
    }

    if (report != null && report.containsFailedResults()) {
      addResultMessage(failedPersistError());
      return new BooleanField(false);
    }
    addMessages(serviceCommons.updateService(config.pidField(), wfsConfigToServiceProps(config)));
    return new BooleanField(!containsErrorMsgs());
  }

  @Override
  public void validate() {
    super.validate();
    if (containsErrorMsgs()) {
      return;
    }
    addMessages(
        sourceValidationUtils.duplicateSourceNameExists(
            config.sourceNameField(), config.pidField()));
  }

  @Override
  public BooleanField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public List<DataType> getArguments() {
    return ImmutableList.of(config);
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new UpdateWfsConfiguration(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(
        DefaultMessages.FAILED_PERSIST,
        DefaultMessages.NO_EXISTING_CONFIG,
        SourceMessages.DUPLICATE_SOURCE_NAME);
  }
}
