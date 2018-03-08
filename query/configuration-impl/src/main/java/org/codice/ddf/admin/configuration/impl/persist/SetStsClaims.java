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
import org.codice.ddf.admin.configuration.impl.StsConfigurationImpl;
import org.codice.ddf.admin.security.common.fields.sts.StsClaimField;

public class SetStsClaims extends BaseFunctionField<BooleanField> {

  public static final String FUNCTION_NAME = "setStsClaims";
  public static final String DESCRIPTION = "Sets the set of sts claims in the system";

  private StsClaimField.ListImpl stsClaims;
  private ConfigurationManager configurationManager;

  public SetStsClaims(ConfigurationManager configurationManager) {
    super(FUNCTION_NAME, DESCRIPTION);
    this.stsClaims = new StsClaimField.ListImpl().useDefaultRequired();

    this.configurationManager = configurationManager;
  }

  @Override
  public BooleanField performFunction() {
    return BooleanField.of(
        configurationManager.setStsConfiguration(new StsConfigurationImpl(stsClaims.getValue())));
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(stsClaims);
  }

  @Override
  public BooleanField getReturnType() {
    return BooleanField.returnType();
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new SetStsClaims(configurationManager);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }
}
