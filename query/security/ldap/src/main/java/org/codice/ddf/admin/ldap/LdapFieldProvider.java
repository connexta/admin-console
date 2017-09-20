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
package org.codice.ddf.admin.ldap;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.ldap.discover.GetLdapConfigurations;
import org.codice.ddf.admin.ldap.discover.LdapQuery;
import org.codice.ddf.admin.ldap.discover.LdapRecommendedSettings;
import org.codice.ddf.admin.ldap.discover.LdapTestBind;
import org.codice.ddf.admin.ldap.discover.LdapTestClaimMappings;
import org.codice.ddf.admin.ldap.discover.LdapTestConnection;
import org.codice.ddf.admin.ldap.discover.LdapTestDirectorySettings;
import org.codice.ddf.admin.ldap.discover.LdapUserAttributes;
import org.codice.ddf.admin.ldap.persist.CreateLdapConfiguration;
import org.codice.ddf.admin.ldap.persist.DeleteLdapConfiguration;

public class LdapFieldProvider extends BaseFieldProvider {
  public static final String DESCRIPTION = "Facilities for interacting with LDAP servers.";

  private static final String NAME = "ldap";

  private static final String TYPE_NAME = "Ldap";

  // Discovery
  private LdapTestConnection testConnection;

  private LdapTestBind testBind;

  private LdapTestDirectorySettings testSettings;

  private LdapRecommendedSettings recommendedSettings;

  private LdapTestClaimMappings claimMappings;

  private LdapQuery ldapQuery;

  private LdapUserAttributes getUserAttris;

  private GetLdapConfigurations getConfigs;

  // Mutate
  private CreateLdapConfiguration createConfig;

  private DeleteLdapConfiguration deleteConfig;

  private List<Field> ldapDiscoveryFields = Collections.emptyList();

  private List<FunctionField> ldapMutationFields = Collections.emptyList();

  public LdapFieldProvider(ConfiguratorSuite configuratorSuite) {
    super(NAME, TYPE_NAME, DESCRIPTION);
    testConnection = new LdapTestConnection();
    testBind = new LdapTestBind();
    testSettings = new LdapTestDirectorySettings();
    recommendedSettings = new LdapRecommendedSettings();
    claimMappings = new LdapTestClaimMappings(configuratorSuite);

    ldapQuery = new LdapQuery();
    getUserAttris = new LdapUserAttributes();
    getConfigs = new GetLdapConfigurations(configuratorSuite);

    createConfig = new CreateLdapConfiguration(configuratorSuite);
    deleteConfig = new DeleteLdapConfiguration(configuratorSuite);
  }

  @Override
  public List<Field> getDiscoveryFields() {
    return new ImmutableList.Builder<Field>() //
        .addAll(ldapDiscoveryFields)
        .add(testConnection)
        .add(testBind)
        .add(testSettings)
        .add(recommendedSettings)
        .add(claimMappings)
        .add(ldapQuery)
        .add(getUserAttris)
        .add(getConfigs)
        .build();
  }

  @Override
  public List<FunctionField> getMutationFunctions() {
    return new ImmutableList.Builder<FunctionField>() //
        .addAll(ldapMutationFields)
        .add(createConfig)
        .add(deleteConfig)
        .build();
  }

  public void setLdapDiscoveryFields(List<Field> ldapDiscoveryFields) {
    this.ldapDiscoveryFields = ldapDiscoveryFields;
  }

  public void setLdapMutationFields(List<FunctionField> ldapMutationFields) {
    this.ldapMutationFields = ldapMutationFields;
  }
}
