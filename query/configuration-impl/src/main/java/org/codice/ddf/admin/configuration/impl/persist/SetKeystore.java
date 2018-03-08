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
import org.codice.ddf.admin.configuration.impl.fields.KeystoreField;

public class SetKeystore extends BaseFunctionField<BooleanField> {

  public static final String FUNCTION_NAME = "setKeystore";

  public static final String DESCRIPTION = "Sets the keystoreArg of the system.";

  private KeystoreField keystoreArg;

  private ConfigurationManager configurationManager;
  // TODO: tbatie - 3/2/18 - Perform validation on claims

  public SetKeystore(ConfigurationManager configurationManager) {
    super(FUNCTION_NAME, DESCRIPTION);
    keystoreArg = new KeystoreField();

    this.configurationManager = configurationManager;
  }

  @Override
  public BooleanField performFunction() {
    return BooleanField.of(
        configurationManager.setKeystoreConfiguration(keystoreArg.toKeystoreConfiguration()));
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(keystoreArg);
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new SetGuestClaims(configurationManager);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }

  @Override
  public BooleanField getReturnType() {
    return BooleanField.returnType();
  }
}
