package org.codice.ddf.admin.configuration.api.security;

public interface KeystoreConfiguration {

  String getKeystorePassword();

  String getPrivateKeyPassword();

  byte[] getKeystoreFile();
}
