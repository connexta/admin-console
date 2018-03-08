package org.codice.ddf.admin.configuration.impl;

import java.util.List;
import org.codice.ddf.admin.configuration.api.ConfigurationManager;
import org.codice.ddf.admin.configuration.api.DefaultConfigurationProfile;
import org.codice.ddf.admin.configuration.api.security.GuestClaimsConfiguration;
import org.codice.ddf.admin.configuration.api.security.KeystoreConfiguration;
import org.codice.ddf.admin.configuration.api.security.StsConfiguration;
import org.codice.ddf.admin.configuration.api.security.TemporaryUserConfiguration;
import org.codice.ddf.admin.configuration.api.security.TruststoreConfiguration;
import org.codice.ddf.admin.configuration.api.system.SystemInformationConfiguration;
import org.codice.ddf.admin.configuration.api.theme.SystemBannerConfiguration;

public class ConfigurationManagerImpl implements ConfigurationManager {

  @Override
  public boolean setTemporaryUsers(List<TemporaryUserConfiguration> users) {
    return false;
  }

  @Override
  public boolean setGuestClaimsConfiguration(GuestClaimsConfiguration guestClaims) {
    return false;
  }

  @Override
  public boolean setSystemBannerConfiguration(SystemBannerConfiguration systemBannerConfiguration) {
    return false;
  }

  @Override
  public boolean setSystemInformationConfiguration(
      SystemInformationConfiguration systemInformation) {
    return false;
  }

  @Override
  public boolean setKeystoreConfiguration(KeystoreConfiguration keystoreConfiguration) {
    return false;
  }

  @Override
  public boolean setTruststoreConfiguration(TruststoreConfiguration truststoreConfiguration) {
    return false;
  }

  @Override
  public boolean setStsConfiguration(StsConfiguration stsConfiguration) {
    return false;
  }

  @Override
  public List<DefaultConfigurationProfile> getDefaultConfigurationProfiles() {
    // TODO: tbatie - 3/8/18 - Add some default config profiles
    return null;
  }

  @Override
  public List<TemporaryUserConfiguration> getTemporaryUsers() {
    return null;
  }

  @Override
  public GuestClaimsConfiguration getGuestClaimsConfiguration() {
    return null;
  }

  @Override
  public SystemBannerConfiguration getSystemBannerConfiguration() {
    return null;
  }

  @Override
  public SystemInformationConfiguration getSystemInformationConfiguration() {
    return null;
  }

  @Override
  public KeystoreConfiguration getKeystoreConfiguration() {
    return null;
  }

  @Override
  public TruststoreConfiguration getTruststoreConfiguration() {
    return null;
  }

  @Override
  public StsConfiguration getStsConfiguration() {
    return null;
  }
}
