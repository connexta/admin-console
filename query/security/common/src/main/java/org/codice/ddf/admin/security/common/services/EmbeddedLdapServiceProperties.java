/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.security.common.services;

public class EmbeddedLdapServiceProperties {

  public static final String EMBEDDED_LDAP_FEATURE = "opendj-embedded";

  public static final String ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE = "ldap-embedded-default-configs";

  public static final String DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE =
      "ldap-embedded-default-stslogin-config";

  public static final String DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE =
      "ldap-embedded-default-claimshandler-config";
}
