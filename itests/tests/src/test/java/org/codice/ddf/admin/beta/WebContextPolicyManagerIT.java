/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.beta;

import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.POLICY_MANAGER_PID;
import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.WHITE_LIST_CONTEXT;
import static org.codice.ddf.admin.security.common.services.StsServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.debugConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.osgi.framework.Constants.SERVICE_PID;
import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.security.auth.login.AppConfigurationEntry;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.karaf.jaas.config.JaasRealm;
import org.boon.Boon;
import org.codice.ddf.admin.security.common.services.StsServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.itests.common.WaitCondition;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.codice.ddf.security.handler.api.AuthenticationHandler;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationAdmin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.ExtractableResponse;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class WebContextPolicyManagerIT {

    public static final String GRAPHQL_ENDPOINT = "http://localhost:8181/admin/beta/graphql";

    public static final String VARIABLE_RESOURCE_PATH = "/query/wcpm/RequestVariables.json";

    public static final String QUERY_RESOURCE_PATH = "/query/wcpm/query/";

    public static final String MUTATION_RESOURCE_PATH = "/query/wcpm/mutation/";

    private String graphqlVariables;

    public static final String TEST_AUTH_1 = "TEST_AUTH_1";

    public static final String TEST_AUTH_2 = "TEST_AUTH_2";

    public static final String TEST_AUTH_3 = "TEST_AUTH_3";

    public static final List<AuthenticationHandler> MOCK_AUTH_TYPES =
            ImmutableList.of(new MockAuthenticationHandler(TEST_AUTH_1),
                    new MockAuthenticationHandler(TEST_AUTH_2),
                    new MockAuthenticationHandler(TEST_AUTH_3));

    public static final String TEST_REALM_1 = "TEST_REALM_1";

    public static final String TEST_REALM_2 = "TEST_REALM_2";

    public static final String TEST_REALM_3 = "TEST_REALM_3";

    public static final List<JaasRealm> MOCK_REALMS = ImmutableList.of(new MockJaasRealm(
            TEST_REALM_1), new MockJaasRealm(TEST_REALM_2), new MockJaasRealm(TEST_REALM_3));

    public static final String TEST_CLAIM_1 = "TEST_CLAIM_1";

    public static final String TEST_CLAIM_2 = "TEST_CLAIM_2";

    public static final String TEST_CLAIM_3 = "TEST_CLAIM_3";

    public static final String[] MOCK_CLAIMS =
            new String[] {TEST_CLAIM_1, TEST_CLAIM_2, TEST_CLAIM_3};

    @BeforeExam
    public static void beforeClass() {
        registerServices(AuthenticationHandler.class, MOCK_AUTH_TYPES);
        registerServices(JaasRealm.class, MOCK_REALMS);
        MockSts.register(MOCK_CLAIMS);

        System.out.println("\nPrinting registered auth types:");
        getServices(AuthenticationHandler.class).forEach(authType -> System.out.println(authType.getAuthenticationType()));

        System.out.println("\nPrinting registered realms:");
        getServices(JaasRealm.class).forEach(realm -> System.out.println(realm.getName()));

        System.out.println("\nPrinting context policies:");
        getServices(ContextPolicyManager.class).get(0)
                .getAllContextPolicies()
                .forEach(System.out::println);
    }

    @Before
    public void setup() {
        WaitCondition.expect("Schema is ready")
                .within(10L, TimeUnit.SECONDS)
                .until(() -> {
                    ExtractableResponse response = sendGraphQlQuery("GetWhiteListed.graphql");
                    System.out.println("\nResponse:\n" + response.body()
                            .asString());
                    return response.jsonPath()
                            .get("data") != null;
                });
    }

    // TODO: tbatie - 7/6/17 - Write test for ensuring:
    //  policies bin collapse
    /*
    @Test
    public void getAuthTypes() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("GetAuthTypes.graphql");
        System.out.println("\nResponse:\n" + response.body().asString());
    }

    @Test
    public void getRealms() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("GetRealms.graphql");
        System.out.println("\nResponse:\n" + response.body().asString());
        assertThat(response.jsonPath()
                .get("data.wcpm.realms"), is(MOCK_REALMS));
    }

    @Test
    public void getPoliciesQuery() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("GetPolicies.graphql");
        System.out.println("\nResponse:\n" + response.body().asString());
    }



    @Test
    public void savePolicies() throws IOException {
        ExtractableResponse response = sendGraphQlMutation("SavePolicies.graphql");
        System.out.println("\nResponse:\n" + response.body().asString());
    }*/


    @Test
    public void getWhiteListed() throws IOException {
        List<String> whitelist = ImmutableList.of("a", "b", "c");

        Map<String, Object> config = new HashMap<>();
        config.put(WHITE_LIST_CONTEXT,
                whitelist.stream()
                        .toArray(String[]::new));
        getServices(ServiceActions.class).get(0)
                .build(POLICY_MANAGER_PID, config, true)
                .commit();

        ExtractableResponse response = sendGraphQlQuery("GetWhiteListed.graphql");
        System.out.println("\nResponse:\n" + response.body()
                .asString());

        assertThat(response.jsonPath()
                .get("data.wcpm.whitelisted"), is(whitelist));
    }

    @Test
    public void saveWhiteListed() throws IOException {
        List<String> whitelist = ImmutableList.of("/services/SecurityTokenService",
                "/services/internal/metrics",
                "/proxy",
                "/services/saml",
                "/services/idp",
                "/idp",
                "/services/platform/config/ui",
                "/logout");

        ExtractableResponse response = sendGraphQlMutation("SaveWhiteListed.graphql");
        System.out.println("\nResponse:\n" + response.body().asString());

        assertThat(getServices(ServiceActions.class).get(0)
                .read(POLICY_MANAGER_PID)
                .get(WHITE_LIST_CONTEXT), is(whitelist));
    }

    public String getGraphQlVariables() {
        if (graphqlVariables == null) {
            graphqlVariables = getResourceAsString(VARIABLE_RESOURCE_PATH);
        }

        return graphqlVariables;
    }

    public ExtractableResponse sendGraphQlQuery(String queryFileName) {
        String body = Boon.toJson(ImmutableMap.of("query",
                getResourceAsString(QUERY_RESOURCE_PATH + queryFileName),
                "variables",
                getGraphQlVariables()));

        System.out.println("\nRequest: \n" + body);
        return given().when()
                .body(body)
                .post(GRAPHQL_ENDPOINT)
                .then()
                .extract();
    }

    public ExtractableResponse sendGraphQlMutation(String mutationFileName) {
        String body = Boon.toJson(ImmutableMap.of("query",
                getResourceAsString(MUTATION_RESOURCE_PATH + mutationFileName),
                "variables",
                getGraphQlVariables()));

        System.out.println("\nRequest: \n" + body);

        return given().when()
                .body(body)
                .post(GRAPHQL_ENDPOINT)
                .then()
                .extract();
    }

    public static String getResourceAsString(String filePath) {
        try {
            return IOUtils.toString(WebContextPolicyManagerIT.class.getClassLoader()
                    .getResourceAsStream(filePath), "UTF-8");
        } catch (IOException e) {
            fail("Unable to retrieve resource: " + filePath);
        }

        return null;
    }

    public static class MockSts {

        private Dictionary<String, String> serviceProps;

        private Dictionary<String, Object> configProps;

        public MockSts(String[] stsClaims) {
            serviceProps = new Hashtable<>();
            serviceProps.put(SERVICE_PID, StsServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID);

            configProps = new Hashtable<>();
            configProps.put(STS_CLAIMS_PROPS_KEY_CLAIMS, stsClaims);
        }

        public Dictionary<String, String> getServiceProps() {
            return serviceProps;
        }

        public Dictionary<String, Object> getConfigProps() {
            return configProps;
        }

        public static void register(String[] stsClaims) {
            MockSts mock = new MockSts(stsClaims);
            getBundleContext().registerService(MockSts.class,
                    new MockSts(stsClaims),
                    mock.getServiceProps());
            try {
                getBundleContext().getService(getBundleContext().getServiceReference(
                        ConfigurationAdmin.class))
                        .getConfiguration(StsServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID)
                        .update(mock.getConfigProps());

            } catch (IOException e) {
                fail("Failed to retrieve configuration for mock sts.");
            }

        }
    }

    public static class MockAuthenticationHandler implements AuthenticationHandler {

        private String authType;

        public MockAuthenticationHandler(String authType) {
            this.authType = authType;
        }

        @Override
        public String getAuthenticationType() {
            return authType;
        }

        @Override
        public HandlerResult getNormalizedToken(ServletRequest servletRequest,
                ServletResponse servletResponse, FilterChain filterChain, boolean b)
                throws ServletException {
            return null;
        }

        @Override
        public HandlerResult handleError(ServletRequest servletRequest,
                ServletResponse servletResponse, FilterChain filterChain) throws ServletException {
            return null;
        }
    }

    public static class MockJaasRealm implements JaasRealm {

        private String realm;

        public MockJaasRealm(String realm) {
            this.realm = realm;
        }

        @Override
        public String getName() {
            return realm;
        }

        @Override
        public int getRank() {
            return 0;
        }

        @Override
        public AppConfigurationEntry[] getEntries() {
            return new AppConfigurationEntry[0];
        }
    }

    // ------------ PAX-EXAM SETTINGS ------------
    public static final String ITEST_FEATURE_URL = "file://" + new File(
            "target/test-classes/features.xml").getAbsolutePath();

    public static final MavenArtifactUrlReference KARAF_DISTRO_URL = maven().groupId(
            "org.apache.karaf")
            .artifactId("apache-karaf")
            .version("4.1.1")
            .type("tar.gz");

    public static final MavenArtifactUrlReference PAX_WEB_FEATURE_URL = maven().groupId(
            "org.ops4j.pax.web")
            .artifactId("pax-web-features")
            .type("xml")
            .classifier("features")
            .version("4.3.0");

    public static final MavenArtifactUrlReference ADMIN_APP_FEATURE_URL = maven().groupId(
            "org.codice.ddf.admin.beta")
            .artifactId("admin-query-app")
            .type("xml")
            .classifier("features")
            .version("0.1.3-SNAPSHOT");

    public static final MavenArtifactUrlReference KARAF_FEATUERS = maven().groupId(
            "org.apache.karaf.features")
            .artifactId("standard")
            .classifier("features")
            .type("xml")
            .version("4.1.1");

    @Configuration
    public Option[] config() {
        return combineOptions(distributionSettings(), bootFeatures(), configurableSettings());
    }

    public Option[] distributionSettings() {
        return new Option[] {debugConfiguration("50005", Boolean.getBoolean("isDebugEnabled")),
                karafDistributionConfiguration().frameworkUrl(KARAF_DISTRO_URL)
                        .unpackDirectory(new File("target/exam")).useDeployFolder(false)};
    }

    public Option[] bootFeatures() {
        return new Option[] {features(KARAF_FEATUERS, "standard"), features(ITEST_FEATURE_URL,
                "test-dependencies",
                "security-core-api",
                "security-handler-api",
                "mock-configurator",
                "security-policy-context"), features(PAX_WEB_FEATURE_URL, "pax-http-whiteboard"),
                features(ADMIN_APP_FEATURE_URL,
                        "admin-beta-graphql",
                        "admin-beta-utils",
                        "admin-beta-wcpm")};
    }

    public Option[] configurableSettings() {
        return new Option[] {keepRuntimeFolder(), logLevel(LogLevelOption.LogLevel.INFO),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
                        "rmiRegistryPort",
                        "20001"), editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
                "rmiServerPort",
                "20002")};
    }

    public Option[] combineOptions(Option[]... options) {
        return Arrays.stream(options)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toArray(Option[]::new);
    }

    public static <T> void registerServices(Class<T> clazz, List<T> instances) {
        instances.forEach(instance -> registerService(clazz, instance));
    }

    public static <T> void registerService(Class<T> clazz, T instance) {
        getBundleContext().registerService(clazz, instance, new Hashtable<>());
    }

    public static <T> List<T> getServices(Class<T> clazz) {
        try {
            return getBundleContext().getServiceReferences(clazz, null)
                    .stream()
                    .map(ref -> getBundleContext().getService(ref))
                    .collect(Collectors.toList());

        } catch (InvalidSyntaxException e) {
            fail(e.getMessage());
        }

        return new ArrayList<>();
    }

    public static BundleContext getBundleContext() {
        return FrameworkUtil.getBundle(WebContextPolicyManagerIT.class)
                .getBundleContext();
    }
}
