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
package org.codice.ddf.admin.security.wcpm.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.invalidPathTrailingSlash;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties;

public class SaveWhitelistContexts extends BaseFunctionField<ContextPath.ListImpl> {

  public static final String FIELD_NAME = "saveWhitelistContexts";

  public static final String DESCRIPTION =
      "Persists the given contexts paths as white listed contexts. White listing a context path will result in no security being applied to the given paths.";

  public static final ContextPath.ListImpl RETURN_TYPE = new ContextPath.ListImpl();

  private static final Pattern TRAILING_SLASH_PATTERN = Pattern.compile("^.+/$");

  private ContextPath.ListImpl contexts;

  private final ConfiguratorSuite configuratorSuite;

  public SaveWhitelistContexts(ConfiguratorSuite configuratorSuite) {
    super(FIELD_NAME, DESCRIPTION);
    this.configuratorSuite = configuratorSuite;

    contexts = new ContextPath.ListImpl();
  }

  @Override
  public void validate() {
    super.validate();
    checkForTrailingSlashes();
  }

  @Override
  public ContextPath.ListImpl performFunction() {
    checkForTrailingSlashes();

    Configurator configurator = configuratorSuite.getConfiguratorFactory().getConfigurator();
    configurator.add(
        configuratorSuite
            .getServiceActions()
            .build(
                PolicyManagerServiceProperties.POLICY_MANAGER_PID,
                new PolicyManagerServiceProperties().whiteListToPolicyManagerProps(contexts),
                true));

    OperationReport configReport = configurator.commit("Whitelist Contexts saved.");

    if (configReport.containsFailedResults()) {
      addErrorMessage(failedPersistError());
    }

    return configReport.containsFailedResults() ? null : contexts;
  }

  @Override
  public ContextPath.ListImpl getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(contexts);
  }

  @Override
  public SaveWhitelistContexts newInstance() {
    return new SaveWhitelistContexts(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(
        DefaultMessages.FAILED_PERSIST, DefaultMessages.INVALID_PATH_TRAILING_SLASH);
  }

  private void checkForTrailingSlashes() {
    contexts
        .getList()
        .stream()
        .filter(contextPath -> TRAILING_SLASH_PATTERN.matcher(contextPath.getValue()).matches())
        .forEach(contextPath -> addErrorMessage(invalidPathTrailingSlash(contextPath.getPath())));
  }
}
