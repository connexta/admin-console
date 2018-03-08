package org.codice.ddf.admin.configuration.api.system;

public interface SystemInformationConfiguration {

  String getHostName();

  Integer getHttpPort();

  Integer getHttpsPort();

  String getSiteContact();

  String getSiteName();

  String getVersion();
}
