package org.codice.ddf.admin.configuration.impl;

import org.codice.ddf.admin.configuration.api.security.KeystoreConfiguration;

public class KeystoreConfigurationImpl implements KeystoreConfiguration {

  private String keystorePassword;
  private String privateKeyPassword;
  private byte[] file;

  public KeystoreConfigurationImpl(
      String keystorePassword, String privateKeyPassword, byte[] file) {
    this.keystorePassword = keystorePassword;
    this.privateKeyPassword = privateKeyPassword;
    this.file = file;
  }

  @Override
  public String getKeystorePassword() {
    return keystorePassword;
  }

  @Override
  public String getPrivateKeyPassword() {
    return privateKeyPassword;
  }

  @Override
  public byte[] getKeystoreFile() {
    return file;
  }
}
