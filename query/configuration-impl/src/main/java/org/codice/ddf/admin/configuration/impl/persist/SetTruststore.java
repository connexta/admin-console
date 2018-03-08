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
import org.codice.ddf.admin.configuration.impl.fields.TruststoreField;

public class SetTruststore extends BaseFunctionField<BooleanField> {

  public static final String FUNCTION_NAME = "setTruststore";

  public static final String DESCRIPTION = "Sets the trust store of the system";

  private ConfigurationManager configurationManager;

  private TruststoreField truststoreArg;

  public SetTruststore(ConfigurationManager configurationManager) {
    super(FUNCTION_NAME, DESCRIPTION);
    this.truststoreArg = new TruststoreField();

    this.configurationManager = configurationManager;
  }

  @Override
  public BooleanField performFunction() {
    return BooleanField.of(
        configurationManager.setTruststoreConfiguration(truststoreArg.toConfig()));
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(truststoreArg);
  }

  @Override
  public BooleanField getReturnType() {
    return BooleanField.returnType();
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new SetTruststore(configurationManager);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }
}
