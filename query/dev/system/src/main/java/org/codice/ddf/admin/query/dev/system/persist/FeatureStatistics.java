package org.codice.ddf.admin.query.dev.system.persist;

import static org.codice.ddf.admin.query.dev.system.DeveloperMessages.failedFeatureInstall;
import static org.codice.ddf.admin.query.dev.system.DeveloperMessages.failedFeatureUninstall;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.karaf.bundle.core.BundleService;
import org.apache.karaf.features.FeaturesService;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.query.dev.system.dependency.WaitForBundles;
import org.codice.ddf.admin.query.dev.system.graph.BundleStateField;
import org.codice.ddf.admin.query.dev.system.dependency.BundleStateRecorder;
import org.codice.ddf.admin.query.dev.system.graph.FeatureStatisticsReport;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class FeatureStatistics extends BaseFunctionField<FeatureStatisticsReport> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureStatistics.class);

  public static final String FUNCTION_NAME = "featureStatistics";

  private static final String DESCRIPTION =
          "Saves statistics about a features installation time such as the bundles states over a period of time.";

  private static final FeatureStatisticsReport RETURN_TYPE = new FeatureStatisticsReport();

  public static final String FEATURE_NAME = "featureName";

  private StringField featureName;

  private FeaturesService featuresService;

  private BundleService bundleService;

  public FeatureStatistics(FeaturesService featuresService, BundleService bundleService) {
    super(FUNCTION_NAME, DESCRIPTION);
    featureName = new StringField(FEATURE_NAME).isRequired(true);

    this.featuresService = featuresService;
    this.bundleService = bundleService;
  }

  @Override
  public FeatureStatisticsReport performFunction() {
    RecordBundleStatesTask installBundleStatesRecorder = new RecordBundleStatesTask();
    RecordBundleStatesTask uninstallBundleStatesRecorder = new RecordBundleStatesTask();
    FeatureStatisticsReport report = new FeatureStatisticsReport();

    Timer installTimer = new Timer();
    try {
      report.installStartTime(System.currentTimeMillis());
      installTimer.schedule(installBundleStatesRecorder, 0, TimeUnit.SECONDS.toMillis(1));
      featuresService.installFeature(featureName.getValue(),
              EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
      WaitForBundles.waitForAllBundles();
      report.installFinishTime(System.currentTimeMillis());
    } catch (Throwable e) {
      LOGGER.error("Failed to install feature.", e);
      addErrorMessage(failedFeatureInstall());
      return report;
    } finally {
      installTimer.cancel();
    }
    report.installBundleStates(installBundleStatesRecorder.getBundleStates());

    Timer uninstallTimer = new Timer();

    try {
      report.uninstallStartTime(System.currentTimeMillis());
      uninstallTimer.schedule(uninstallBundleStatesRecorder, 0, TimeUnit.SECONDS.toMillis(1));
      featuresService.uninstallFeature(featureName.getValue(),
              EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
      WaitForBundles.waitForAllBundles();
      report.uninstallFinishTime(System.currentTimeMillis());
    } catch (Throwable e) {
      LOGGER.error("Failed to uninstall feature.", e);
      addErrorMessage(failedFeatureUninstall());
    } finally {
      uninstallTimer.cancel();
    }

    report.uninstallBundleStates(uninstallBundleStatesRecorder.getBundleStates());
    return report;
  }

  public static class RecordBundleStatesTask extends TimerTask {

    private Map<String, BundleStateRecorder> states;

    public RecordBundleStatesTask() {
      this.states = getBundles();
    }

    @Override
    public void run() {
      Map<String, BundleStateRecorder> newStates = getBundles();
      if (!states.keySet().containsAll(newStates.keySet())) {
        states.keySet().forEach(newStates::remove);
        states.putAll(newStates);
      }

      long currentTime = scheduledExecutionTime();
      newStates.values()
              .parallelStream()
              .forEach(r -> r.recordState(currentTime));
    }

    private Map<String, BundleStateRecorder> getBundles() {
      return Arrays.stream(FrameworkUtil.getBundle(FeatureStatistics.class)
              .getBundleContext()
              .getBundles())
              .filter(b -> b.getHeaders()
                      .get("Fragment-Host") == null)
              .collect(Collectors.toMap(Bundle::getLocation, BundleStateRecorder::new));
    }

    /**
     * Returns a map where the location is the key and the value is the states of that bundle during feature install
     * @return
     */
    public List<BundleStateField> getBundleStates() {
      return states.values()
              .stream()
              .flatMap(e -> e.getPreviousStates().stream())
              .collect(Collectors.toList());
    }
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(featureName);
  }

  @Override
  public FeatureStatisticsReport getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField<FeatureStatisticsReport> newInstance() {
    return new FeatureStatistics(featuresService, bundleService);
  }
}
