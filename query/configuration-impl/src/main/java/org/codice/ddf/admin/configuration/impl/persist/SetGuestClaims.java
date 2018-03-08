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
import org.codice.ddf.admin.configuration.api.security.GuestClaimsConfiguration;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;

public class SetGuestClaims extends BaseFunctionField<BooleanField> {

  public static final String FUNCTION_NAME = "setGuestClaims";

  public static final String DESCRIPTION = "Sets the guest claims mapping for guest users.";

  private ClaimsMapEntry.ListImpl claimsMapping;

  private ConfigurationManager configurationManager;
  // TODO: tbatie - 3/2/18 - Perform validation on claims

  public SetGuestClaims(ConfigurationManager configurationManager) {
    super(FUNCTION_NAME, DESCRIPTION);
    claimsMapping = new ClaimsMapEntry.ListImpl().useDefaultRequired();

    this.configurationManager = configurationManager;
  }

  @Override
  public BooleanField performFunction() {
    GuestClaimsConfiguration guestClaimsConfiguration = () -> claimsMapping.toMap();
    return BooleanField.of(
        configurationManager.setGuestClaimsConfiguration(guestClaimsConfiguration));
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(claimsMapping);
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
