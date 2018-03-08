package org.codice.ddf.admin.configuration.impl;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.configuration.api.ConfigurationManager;
import org.codice.ddf.admin.configuration.impl.discover.GetConfigurationProfiles;
import org.codice.ddf.admin.configuration.impl.persist.SetGuestClaims;
import org.codice.ddf.admin.configuration.impl.persist.SetKeystore;
import org.codice.ddf.admin.configuration.impl.persist.SetStsClaims;
import org.codice.ddf.admin.configuration.impl.persist.SetSystemBanner;
import org.codice.ddf.admin.configuration.impl.persist.SetSystemInformation;
import org.codice.ddf.admin.configuration.impl.persist.SetTemporaryUsers;
import org.codice.ddf.admin.configuration.impl.persist.SetTruststore;

public class ConfigurationFieldProvider extends BaseFieldProvider {

  public static final String FIELD_NAME = "configs";

  public static final String FIELD_TYPE_NAME = "Configurations";

  public static final String DESCRIPTION = "Contains the configurations of the system.";

  private GetConfigurationProfiles getConfigurationProfiles;

  private SetGuestClaims setGuestClaims;

  private SetKeystore setKeystore;

  private SetStsClaims setStsClaims;

  private SetSystemBanner setSystemBanner;

  private SetSystemInformation setSystemInformation;

  private SetTemporaryUsers setTemporaryUsers;

  private SetTruststore setTruststore;

  public ConfigurationFieldProvider(ConfigurationManager configurationManager) {
    super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    this.getConfigurationProfiles = new GetConfigurationProfiles(configurationManager);

    this.setGuestClaims = new SetGuestClaims(configurationManager);
    this.setKeystore = new SetKeystore(configurationManager);
    this.setStsClaims = new SetStsClaims(configurationManager);
    this.setSystemBanner = new SetSystemBanner(configurationManager);
    this.setTemporaryUsers = new SetTemporaryUsers(configurationManager);
    this.setTruststore = new SetTruststore(configurationManager);
  }

  @Override
  public List<FunctionField> getDiscoveryFunctions() {
    return ImmutableList.of(getConfigurationProfiles);
  }

  @Override
  public List<FunctionField> getMutationFunctions() {
    return ImmutableList.of(
        setGuestClaims,
        setKeystore,
        setStsClaims,
        setSystemBanner,
        setTemporaryUsers,
        setTruststore);
  }
}
