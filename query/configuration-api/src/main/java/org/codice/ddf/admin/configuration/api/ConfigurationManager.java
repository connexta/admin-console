package org.codice.ddf.admin.configuration.api;

import java.util.List;
import org.codice.ddf.admin.configuration.api.security.GuestClaimsConfiguration;
import org.codice.ddf.admin.configuration.api.security.KeystoreConfiguration;
import org.codice.ddf.admin.configuration.api.security.StsConfiguration;
import org.codice.ddf.admin.configuration.api.security.TemporaryUserConfiguration;
import org.codice.ddf.admin.configuration.api.security.TruststoreConfiguration;
import org.codice.ddf.admin.configuration.api.system.SystemInformationConfiguration;
import org.codice.ddf.admin.configuration.api.theme.SystemBannerConfiguration;

public interface ConfigurationManager {

  boolean setTemporaryUsers(List<TemporaryUserConfiguration> users);

  boolean setGuestClaimsConfiguration(GuestClaimsConfiguration guestClaims);

  boolean setSystemBannerConfiguration(SystemBannerConfiguration systemBannerConfiguration);

  boolean setSystemInformationConfiguration(SystemInformationConfiguration systemInformation);

  boolean setKeystoreConfiguration(KeystoreConfiguration keystoreConfiguration);

  boolean setTruststoreConfiguration(TruststoreConfiguration truststoreConfiguration);

  boolean setStsConfiguration(StsConfiguration stsConfiguration);

  List<DefaultConfigurationProfile> getDefaultConfigurationProfiles();

  List<TemporaryUserConfiguration> getTemporaryUsers();

  GuestClaimsConfiguration getGuestClaimsConfiguration();

  SystemBannerConfiguration getSystemBannerConfiguration();

  SystemInformationConfiguration getSystemInformationConfiguration();

  KeystoreConfiguration getKeystoreConfiguration();

  TruststoreConfiguration getTruststoreConfiguration();

  StsConfiguration getStsConfiguration();
}
