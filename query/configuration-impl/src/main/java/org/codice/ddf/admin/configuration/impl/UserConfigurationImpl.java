package org.codice.ddf.admin.configuration.impl;

import java.util.List;
import java.util.Map;
import org.codice.ddf.admin.configuration.api.security.TemporaryUserConfiguration;

public class UserConfigurationImpl implements TemporaryUserConfiguration {

  private String name;
  private String password;
  private List<String> roles;
  private Map<String, String> claimsMapping;

  public UserConfigurationImpl(
      String name, String password, List<String> roles, Map<String, String> claimsMapping) {
    this.name = name;
    this.password = password;
    this.roles = roles;
    this.claimsMapping = claimsMapping;
  }

  @Override
  public String getUsername() {
    return name;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public List<String> getRoles() {
    return roles;
  }

  @Override
  public Map<String, String> getClaimsMapping() {
    return claimsMapping;
  }
}
