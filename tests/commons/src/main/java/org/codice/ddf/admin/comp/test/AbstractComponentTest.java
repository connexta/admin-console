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
package org.codice.ddf.admin.comp.test;

import static junit.framework.TestCase.fail;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.debugConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.apache.karaf.features.BootFinished;
import org.codice.ddf.itests.common.AdminConfig;
import org.codice.ddf.itests.common.ServiceManager;
import org.codice.ddf.itests.common.ServiceManagerImpl;
import org.codice.ddf.itests.common.ServiceManagerProxy;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.codice.ddf.itests.common.annotations.PaxExamRule;
import org.codice.ddf.itests.common.security.SecurityPolicyConfigurator;
import org.junit.Rule;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.metatype.MetaTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractComponentTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentTest.class);

  @Rule public PaxExamRule paxExamRule = new PaxExamRule(this);

  /** To make sure the tests run only when the boot features are fully installed */
  @Inject BootFinished bootFinished;

  @Inject protected ConfigurationAdmin configurationAdmin;

  @Inject protected MetaTypeService metatypeService;

  protected AdminConfig adminConfig;

  protected ServiceManager serviceManager;

  protected SecurityPolicyConfigurator securityPolicyConfigurator;

  /**
   * Adds the given FeatureFile to the feature-repo. If the FeatureFile is an instance of a Feature,
   * it will be started when the test begins.
   *
   * @return
   */
  protected abstract List<Feature> features();

  @Configuration
  public Option[] config() throws Exception {
    List<Option> features =
        features()
            .stream()
            .map(
                feature -> {
                  if (feature.isBootFeature()) {
                    return KarafDistributionOption.features(
                        feature.getUrl(), feature.getFeatureName());
                  } else {
                    return KarafDistributionOption.features(feature.getUrl());
                  }
                })
            .collect(Collectors.toList());

    return Stream.of(
            distributionSettings(),
            features,
            configurableSettings(),
            configureVmOptions(),
            customSettings())
        .flatMap(Collection::stream)
        .toArray(Option[]::new);
  }

  protected List<Option> customSettings() {
    return new ArrayList<>();
  }

  protected List<Option> distributionSettings() {
    return Arrays.asList(
        debugConfiguration("50005", Boolean.getBoolean("isDebugEnabled")),
        karafDistributionConfiguration()
            .frameworkUrl(
                maven()
                    .groupId("org.codice.ddf")
                    .artifactId("kernel")
                    .versionAsInProject()
                    .type("zip"))
            .unpackDirectory(new File("target/exam"))
            .useDeployFolder(false),
        cleanCaches());
  }

  protected List<Option> configurableSettings() throws Exception {
    return Arrays.asList(
        keepRuntimeFolder(),
        logLevel(LogLevelOption.LogLevel.INFO),

        // TODO: tbatie - 8/15/17 - Change these to dynamic port once refactored
        editConfigurationFilePut("etc/system.properties", "ddf.home", "${karaf.home}"),
        editConfigurationFilePut(
            "etc/system.properties", "org.codice.ddf.system.httpsPort", "9993"),
        editConfigurationFilePut("etc/system.properties", "org.codice.ddf.system.httpPort", "9994"),
        editConfigurationFilePut(
            "etc/system.properties", "org.codice.ddf.catalog.ftp.port", "9995"),
        editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "20001"),
        editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "20002"),
        editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "20002"),
        editConfigurationFilePut(
            "etc/org.apache.karaf.features.cfg", "featuresBootAsynchronous", "true"));
  }

  @BeforeExam
  public void beforeExam() throws Exception {
    adminConfig = new AdminConfig(configurationAdmin);
    serviceManager =
        (ServiceManager)
            Proxy.newProxyInstance(
                getClass().getClassLoader(),
                ServiceManagerImpl.class.getInterfaces(),
                new ServiceManagerProxy(new ServiceManagerImpl(metatypeService, adminConfig)));

    securityPolicyConfigurator =
        new SecurityPolicyConfigurator(
            new ServiceManagerImpl(metatypeService, adminConfig), configurationAdmin);
    startFeatures();
  }

  protected List<Option> configureVmOptions() {
    return Arrays.asList(
        vmOption("-Xmx4096M"),
        vmOption("-Xms2048M"),
        // avoid integration tests stealing focus on OS X
        vmOption("-Djava.awt.headless=true"),
        vmOption("-Dfile.encoding=UTF8"));
  }

  protected void startFeatures() throws Exception {
    features()
        .stream()
        .parallel()
        .filter(f -> !f.isBootFeature() && f.getFeatureName() != null)
        .map(Feature::getFeatureName)
        .forEach(
            feature -> {
              try {
                serviceManager.startFeature(true, feature);
              } catch (Exception e) {
                LOGGER.error("Failed to start up feature: " + feature, e);
                fail("Failed to start feature: " + feature);
              }
            });
  }
}
