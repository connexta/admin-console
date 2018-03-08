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
import org.codice.ddf.admin.configuration.impl.fields.TemporaryUser;

public class SetTemporaryUsers extends BaseFunctionField<BooleanField> {

  public static final String FUNCTION_NAME = "setTemporaryUsers";

  public static final String DESCRIPTION = "Sets the number of temporary users in the system.";

  private TemporaryUser.ListImpl tempUsers;

  private ConfigurationManager configManager;

  public SetTemporaryUsers(ConfigurationManager configManager) {
    super(FUNCTION_NAME, DESCRIPTION);
    tempUsers = new TemporaryUser.ListImpl().useDefaultRequired();
    this.configManager = configManager;
  }

  @Override
  public BooleanField performFunction() {
    return BooleanField.of(configManager.setTemporaryUsers(tempUsers.toConfigs()));
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(tempUsers);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }

  @Override
  public BooleanField getReturnType() {
    return BooleanField.returnType();
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new SetTemporaryUsers(configManager);
  }
}
