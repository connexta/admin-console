package org.codice.ddf.admin.configuration.api.security;

import java.util.Map;

public interface GuestClaimsConfiguration {

  Map<String, String> getClaimsMapping();
}
