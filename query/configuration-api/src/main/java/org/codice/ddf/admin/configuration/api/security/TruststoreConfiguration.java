package org.codice.ddf.admin.configuration.api.security;

public interface TruststoreConfiguration {

  String getTruststorePassword();

  byte[] getTruststoreFile();
}
