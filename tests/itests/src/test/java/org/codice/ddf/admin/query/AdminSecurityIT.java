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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.comp.test.AbstractComponentTest;
import org.codice.ddf.admin.comp.test.AdminQueryAppFeatureFile;
import org.codice.ddf.admin.comp.test.ComponentTestFeatureFile;
import org.codice.ddf.admin.comp.test.Feature;
import org.codice.ddf.admin.comp.test.PlatformAppFeatureFile;
import org.codice.ddf.admin.comp.test.SecurityAppFeatureFile;
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
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AdminSecurityIT extends AbstractComponentTest {
  @Override
  public List<Option> customSettings() {
    return super.customSettings();
  }

  // TODO: tbatie - 8/12/17 - Figure out why the SCR bunlde is taking so long

  // TODO: tbatie - 8/19/17 - Fix port once dynamic port is refactored for general testing
  public static final String GRAPHQL_ENDPOINT = "https://localhost:9993/admin/hub/graphql";

  public static final String TEST_CONTEXT_PATH = "/testing";

  public static final String TEST_CONTEXT_PATH_2 = "/testing/2";

  public static final String TEST_REALM = "karaf";

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

  public static ServiceReader serviceReader;

  @Override
  public List<Feature> features() {
    // TODO: tbatie - 8/19/17 - Fix all these features once they've been fixed in DDF. We should
    // only have to call our admin-console-app feature
    return Arrays.asList(
        PlatformAppFeatureFile.featureFile(),
        SecurityAppFeatureFile.featureFile(),
        ComponentTestFeatureFile.thirdPartyFeature().bootFeature(),
        ComponentTestFeatureFile.commonTestDependenciesFeature().bootFeature(),
        AdminQueryAppFeatureFile.adminCoreFeature().bootFeature(),
        //  add so pax exam has access to data type api before being started

        // Added a boot feature because the tests need the ServiceManager running before we can
        // startFeatures
        ComponentTestFeatureFile.securityAll().bootFeature(),
        ComponentTestFeatureFile.configuratorFeature(),
        ComponentTestFeatureFile.configSecurityPolicy(),
        ComponentTestFeatureFile.ldapClaimsHandlerFeature(),
        ComponentTestFeatureFile.ldapLoginFeature(),
        AdminQueryAppFeatureFile.adminSecurityFeature(),
        AdminQueryAppFeatureFile.adminGraphQlFeature());
  }

  @BeforeExam
  @Override
  public void beforeExam() throws Exception {
    super.beforeExam();
    securityPolicyConfigurator.configureRestForBasic();

    WCPM_REQUEST_HELPER.waitForWcpmInSchema();
    LDAP_REQUEST_HELPER.waitForLdapInSchema();
    serviceReader = serviceManager.getService(ServiceReader.class);
  }

  @Test
  public void getAuthTypes() throws Exception {
    assertThat(WCPM_REQUEST_HELPER.getAuthType().isEmpty(), is(false));
  }

  @Test
  public void getRealms() throws IOException {
    assertThat(WCPM_REQUEST_HELPER.getRealms().isEmpty(), is(false));
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
   *
   * @throws IOException
   */
  @Test
  public void savePolicies() throws IOException {

    // TODO: tbatie - 8/22/17 - Testing whether the bins collapse should be done at the unit test
    // level.
    try {
      Map<String, Object> newPolicy1 =
          new ContextPolicyBin(serviceReader)
              .addContextPath(TEST_CONTEXT_PATH)
              .realm(TEST_REALM)
              .addAuthType(TEST_AUTH_TYPE)
              .addClaimsMapping(TEST_CLAIM_KEY, TEST_CLAIM_VALUE)
              .getValue();

      Map<String, Object> newPolicy2 =
          new ContextPolicyBin(serviceReader)
              .addContextPath(TEST_CONTEXT_PATH_2)
              .realm(TEST_REALM)
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
              .realm(TEST_REALM)
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

    LdapDirectorySettingsField dirSettings =
        new LdapDirectorySettingsField()
            .baseUserDn(TEST_DN)
            .usernameAttribute(TEST_ATTRIBUTE)
            .groupAttributeHoldingMember(TEST_ATTRIBUTE)
            .baseGroupDn(TEST_DN)
            .useCase(ldapUseCase.getValue());

    if (ldapUseCase.getValue().equals(LdapUseCase.ATTRIBUTE_STORE.getValue())
        || ldapUseCase
            .getValue()
            .equals(LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE.getValue())) {
      dirSettings.groupObjectClass(TEST_ATTRIBUTE).memberAttributeReferencedInGroup(TEST_ATTRIBUTE);

      newConfig.claimMappingsField(
          new ClaimsMapEntry.ListImpl()
              .add(new ClaimsMapEntry().key(TEST_CLAIM_KEY).value(TEST_CLAIM_VALUE)));
    }

    return newConfig.connection(connection).bindUserInfo(bindUserInfo).settings(dirSettings);
  }
}
