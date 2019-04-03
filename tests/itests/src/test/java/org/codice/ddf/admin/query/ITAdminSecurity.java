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
package org.codice.ddf.admin.query;

import static org.codice.ddf.test.common.options.DebugOptions.defaultDebuggingOptions;
import static org.codice.ddf.test.common.options.DistributionOptions.kernelDistributionOption;
import static org.codice.ddf.test.common.options.FeatureOptions.addBootFeature;
import static org.codice.ddf.test.common.options.LoggingOptions.defaultLogging;
import static org.codice.ddf.test.common.options.PortOptions.defaultPortsOptions;
import static org.codice.ddf.test.common.options.PortOptions.getHttpsPort;
import static org.codice.ddf.test.common.options.TestResourcesOptions.getTestResource;
import static org.codice.ddf.test.common.options.TestResourcesOptions.includeTestResources;
import static org.codice.ddf.test.common.options.VmOptions.defaultVmOptions;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.graphql.test.AdminQueryFeatures;
import org.codice.ddf.admin.graphql.test.AdminQueryTestingFeatures;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod.SimpleEnumValue;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField;
import org.codice.ddf.admin.query.request.LdapRequestHelper;
import org.codice.ddf.admin.query.request.WcpmRequestHelper;
import org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;
import org.codice.ddf.sync.installer.api.SynchronizedInstaller;
import org.codice.ddf.test.common.features.AppsFeatures;
import org.codice.ddf.test.common.features.TestUtilitiesFeatures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ITAdminSecurity {

  public static final String ADMIN_QUERY_SECURITY_POLICY = "/admin-query-security.policy";

  public static final File ADMIN_QUERY_SECURITY_POLICY_FILE =
      Paths.get(getTestResource(ADMIN_QUERY_SECURITY_POLICY)).toFile();

  public static final String POLICY_MNGR_CONFIG_PATH =
      "/org.codice.ddf.security.policy.context.impl.PolicyManager.cfg";

  public static final File POLICY_MNGR_CONFIG_FILE =
      Paths.get(getTestResource(POLICY_MNGR_CONFIG_PATH)).toFile();

  public static final String GRAPHQL_ENDPOINT =
      "https://localhost:" + getHttpsPort() + "/admin/hub/graphql";

  public static final String TEST_CONTEXT_PATH = "/testing";

  public static final String TEST_CONTEXT_PATH_2 = "/testing/2";

  public static final String TEST_AUTH_TYPE = "BASIC";

  public static final String TEST_CLAIM_KEY =
      "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role";

  public static final String TEST_CLAIM_VALUE = "test-claim-value";

  public static final String TEST_DN = "ou=users,dc=example,dc=com";

  public static final String TEST_ATTRIBUTE = "testAttribute";

  public static final String TEST_USERNAME = "testUserName";

  public static final String TEST_PASSWORD = "*****";

  public static final WcpmRequestHelper WCPM_REQUEST_HELPER =
      new WcpmRequestHelper(GRAPHQL_ENDPOINT);

  public static final LdapRequestHelper LDAP_REQUEST_HELPER =
      new LdapRequestHelper(GRAPHQL_ENDPOINT);

  @Inject private SynchronizedInstaller syncInstaller;

  @Inject private ServiceReader serviceReader;

  @Configuration
  public static Option[] examConfiguration() {
    return options(
        kernelDistributionOption(),
        defaultVmOptions(),
        defaultDebuggingOptions(),
        defaultPortsOptions(),
        defaultLogging(),
        includeTestResources(),
        //        logLevelOption("org.codice.ddf.graphql", "TRACE"),
        //        logLevelOption("org.codice.ddf.admin.graphql.test", "TRACE"),
        replaceConfigurationFile("/etc/" + POLICY_MNGR_CONFIG_PATH, POLICY_MNGR_CONFIG_FILE),
        replaceConfigurationFile(
            "/security/" + ADMIN_QUERY_SECURITY_POLICY, ADMIN_QUERY_SECURITY_POLICY_FILE),
        addBootFeature(
            TestUtilitiesFeatures.testCommon(),
            AdminQueryTestingFeatures.itestCommons(),
            TestUtilitiesFeatures.awaitility(),
            TestUtilitiesFeatures.hamcrestAll(),
            TestUtilitiesFeatures.restAssured(),
            AppsFeatures.securityServicesApp(),
            AdminQueryFeatures.adminQueryAll()));
  }

  @Before
  public void before() throws Exception {
    syncInstaller.waitForBootFinish();
    WCPM_REQUEST_HELPER.saveWhitelistDefaultValues();
    LDAP_REQUEST_HELPER.waitForLdapInSchema();
  }

  @Test
  public void getAuthTypes() throws Exception {
    assertThat(WCPM_REQUEST_HELPER.getAuthType().isEmpty(), is(false));
  }

  @Test
  public void saveWhiteListed() throws IOException {
    try {
      List<String> expectedWhiteListValues = new ArrayList<>();
      expectedWhiteListValues.addAll(WCPM_REQUEST_HELPER.getWhiteListContexts());
      expectedWhiteListValues.add(TEST_CONTEXT_PATH);

      WCPM_REQUEST_HELPER.saveWhiteListContexts(expectedWhiteListValues);
      WCPM_REQUEST_HELPER.waitForWhiteList(expectedWhiteListValues);
    } finally {
      WCPM_REQUEST_HELPER.resetWhiteList();
    }
  }

  /**
   * For the user's convenience, identical policy bins are collapsed into a single policy bin. This
   * test confirms two policies bins can be persisted and retrieved as a single collapsed policy
   */
  @Test
  public void savePolicies() throws IOException {

    // TODO: tbatie - 8/22/17 - Testing whether the bins collapse should be done at the unit test
    // level.
    try {
      Map<String, Object> newPolicy1 =
          new ContextPolicyBin(serviceReader)
              .addContextPath(TEST_CONTEXT_PATH)
              .addAuthType(TEST_AUTH_TYPE)
              .addClaimsMapping(TEST_CLAIM_KEY, TEST_CLAIM_VALUE)
              .getValue();

      Map<String, Object> newPolicy2 =
          new ContextPolicyBin(serviceReader)
              .addContextPath(TEST_CONTEXT_PATH_2)
              .addAuthType(TEST_AUTH_TYPE)
              .addClaimsMapping(TEST_CLAIM_KEY, TEST_CLAIM_VALUE)
              .getValue();

      List<Map<String, Object>> savedPolicies = new ArrayList<>();
      savedPolicies.addAll(WCPM_REQUEST_HELPER.getInitialPolicies());
      savedPolicies.add(newPolicy1);
      savedPolicies.add(newPolicy2);

      Map<String, Object> expectedCollapsedPolicy =
          new ContextPolicyBin(serviceReader)
              .addContextPath(TEST_CONTEXT_PATH)
              .addContextPath(TEST_CONTEXT_PATH_2)
              .addAuthType(TEST_AUTH_TYPE)
              .addClaimsMapping(TEST_CLAIM_KEY, TEST_CLAIM_VALUE)
              .getValue();

      List<Map<String, Object>> collapsedPolicies = new ArrayList<>();
      collapsedPolicies.addAll(WCPM_REQUEST_HELPER.getInitialPolicies());
      collapsedPolicies.add(expectedCollapsedPolicy);

      WCPM_REQUEST_HELPER.saveContextPolicies(savedPolicies);
      WCPM_REQUEST_HELPER.waitForContextPolicies(collapsedPolicies);
    } finally {
      WCPM_REQUEST_HELPER.resetContextPolicies();
    }
  }

  @Test
  public void saveLdapAuthenticationConfig() throws IOException {
    try {
      LdapConfigurationField newConfig = createSampleLdapConfiguration(LdapUseCase.AUTHENTICATION);
      LDAP_REQUEST_HELPER.createLdapConfig(newConfig);
      LDAP_REQUEST_HELPER.waitForConfigs(Collections.singletonList(newConfig.getValue()), true);
    } finally {
      LDAP_REQUEST_HELPER.resetLdapConfigs();
    }
  }

  @Test
  public void saveLdapAttributeStoreConfig() throws IOException {
    try {
      LdapConfigurationField newConfig = createSampleLdapConfiguration(LdapUseCase.ATTRIBUTE_STORE);
      LDAP_REQUEST_HELPER.createLdapConfig(newConfig);
      LDAP_REQUEST_HELPER.waitForConfigs(Collections.singletonList(newConfig.getValue()), true);
    } finally {
      LDAP_REQUEST_HELPER.resetLdapConfigs();
    }
  }

  @Test
  public void saveLdapAuthenticationAndAttributeStoreConfig() throws IOException {
    try {
      LdapConfigurationField newConfig =
          createSampleLdapConfiguration(LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE);
      List<Map<String, Object>> expectedConfigs =
          ImmutableList.of(
              createSampleLdapConfiguration(LdapUseCase.AUTHENTICATION).getValue(),
              createSampleLdapConfiguration(LdapUseCase.ATTRIBUTE_STORE).getValue());

      LDAP_REQUEST_HELPER.createLdapConfig(newConfig);
      LDAP_REQUEST_HELPER.waitForConfigs(expectedConfigs, true);
    } finally {
      LDAP_REQUEST_HELPER.resetLdapConfigs();
    }
  }

  public LdapConfigurationField createSampleLdapConfiguration(EnumValue<String> ldapUseCase) {
    LdapConfigurationField newConfig = new LdapConfigurationField();

    CredentialsField creds = new CredentialsField().username(TEST_USERNAME).password(TEST_PASSWORD);

    LdapBindUserInfo bindUserInfo =
        new LdapBindUserInfo().bindMethod(SimpleEnumValue.SIMPLE).credentialsField(creds);

    LdapConnectionField connection =
        new LdapConnectionField()
            .encryptionMethod(LdapEncryptionMethodField.NoEncryption.NONE)
            .hostname("testHostName")
            .port(666);

    LdapConnectionField.ListImpl connections = new LdapConnectionField.ListImpl();
    connections.add(connection);

    LdapDirectorySettingsField dirSettings =
        new LdapDirectorySettingsField()
            .baseUserDn(TEST_DN)
            .loginUserAttribute(TEST_ATTRIBUTE)
            .memberAttributeReferencedInGroup(TEST_ATTRIBUTE)
            .baseGroupDn(TEST_DN)
            .groupAttributeHoldingMember(TEST_ATTRIBUTE)
            .useCase(ldapUseCase.getValue());

    if (ldapUseCase.getValue().equals(LdapUseCase.ATTRIBUTE_STORE.getValue())
        || ldapUseCase
            .getValue()
            .equals(LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE.getValue())) {
      dirSettings.groupObjectClass(TEST_ATTRIBUTE);

      newConfig.claimMappingsField(
          new ClaimsMapEntry.ListImpl()
              .add(new ClaimsMapEntry().key(TEST_CLAIM_KEY).value(TEST_CLAIM_VALUE)));
    }

    return newConfig.connections(connections).bindUserInfo(bindUserInfo).settings(dirSettings);
  }
}
