package org.codice.ddf.admin.configuration.impl.persist;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.configuration.api.ConfigurationManager;
import org.codice.ddf.admin.configuration.impl.fields.SystemBannerField;

public class SetSystemBanner extends BaseFunctionField<BooleanField> {

  public static final String FUNCTION_NAME = "setSystemBanner";
  public static final String DESCRIPTION = "Sets the classification of the banner.";

  private ConfigurationManager configurationManager;

  private SystemBannerField systemBannerField;

  public SetSystemBanner(ConfigurationManager configurationManager) {
    super(FUNCTION_NAME, DESCRIPTION);
    this.configurationManager = configurationManager;

    systemBannerField = new SystemBannerField();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(systemBannerField);
  }

  @Override
  public BooleanField getReturnType() {
    return BooleanField.returnType();
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new SetSystemBanner(configurationManager);
  }

  @Override
  public BooleanField performFunction() {
    return BooleanField.of(
        configurationManager.setSystemBannerConfiguration(systemBannerField.toConfig()));
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }
}
