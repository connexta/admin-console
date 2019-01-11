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
package org.codice.ddf.admin.query.features.itest;

import static org.codice.ddf.test.common.options.DebugOptions.defaultDebuggingOptions;
import static org.codice.ddf.test.common.options.DistributionOptions.kernelDistributionOption;
import static org.codice.ddf.test.common.options.FeatureOptions.addBootFeature;
import static org.codice.ddf.test.common.options.FeatureOptions.addFeatureRepo;
import static org.codice.ddf.test.common.options.LoggingOptions.defaultLogging;
import static org.codice.ddf.test.common.options.PortOptions.defaultPortsOptions;
import static org.codice.ddf.test.common.options.TestResourcesOptions.includeTestResources;
import static org.codice.ddf.test.common.options.VmOptions.defaultVmOptions;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Inject;
import org.codice.ddf.sync.installer.api.SynchronizedInstaller;
import org.codice.ddf.test.common.features.FeatureUtilities;
import org.codice.ddf.test.common.features.TestUtilitiesFeatures;
import org.codice.ddf.test.common.options.TestResourcesOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExamParameterized;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExamParameterized.class)
@ExamReactorStrategy(PerClass.class)
public class ITAdminQueryFeatures {

  private static final String FEATURE_REPO_PATH =
      TestResourcesOptions.getTestResource("/feature.xml");

  @Configuration
  public static Option[] examConfiguration() {
    return options(
        kernelDistributionOption(),
        defaultVmOptions(),
        defaultDebuggingOptions(),
        defaultPortsOptions(),
        defaultLogging(),
        includeTestResources(),
        addFeatureRepo(FeatureUtilities.toFeatureRepo(FEATURE_REPO_PATH)),
        addBootFeature(TestUtilitiesFeatures.testCommon()),
        junitBundles());
  }

  @Parameterized.Parameters
  public static List<Object[]> getParameters() {
    return FeatureUtilities.featureRepoToFeatureParameters(FEATURE_REPO_PATH);
  }

  @Inject private SynchronizedInstaller syncInstaller;

  private String featureName;

  @Before
  public void before() {
    System.out.println("Pax Url Config:");
    try {
      for (String line : Files.readAllLines(Paths.get("etc/org.ops4j.pax.url.mvn.repositories"))) {
        System.out.println(line);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public ITAdminQueryFeatures(String featureName) {
    this.featureName = featureName;
  }

  @Test
  public void installAndUninstallFeature() throws Exception {
    syncInstaller.installFeatures(featureName);
    syncInstaller.uninstallFeatures(featureName);
  }
}
