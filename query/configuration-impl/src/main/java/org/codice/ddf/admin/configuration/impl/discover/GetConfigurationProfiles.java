package org.codice.ddf.admin.configuration.impl.discover;

import java.util.Collections;
import java.util.Set;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.configuration.api.ConfigurationManager;
import org.codice.ddf.admin.configuration.impl.fields.ConfigurationProfileField;

public class GetConfigurationProfiles extends GetFunctionField<ConfigurationProfileField.ListImpl> {

  public static final String FUNCTION_NAME = "installationProfiles";

  public static final String DESCRIPTION =
      "Retrieves default profiles recommended for the user to use during an installation.";

  public static final ConfigurationProfileField.ListImpl RETURN_TYPE =
      new ConfigurationProfileField.ListImpl();

  private ConfigurationManager configurationManager;

  public GetConfigurationProfiles(ConfigurationManager configurationManager) {
    super(FUNCTION_NAME, DESCRIPTION);
    this.configurationManager = configurationManager;
  }

  @Override
  public ConfigurationProfileField.ListImpl performFunction() {
    return new ConfigurationProfileField.ListImpl(
        configurationManager.getDefaultConfigurationProfiles());
  }

  @Override
  public ConfigurationProfileField.ListImpl getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField<ConfigurationProfileField.ListImpl> newInstance() {
    return new GetConfigurationProfiles(configurationManager);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }
}
