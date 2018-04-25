package org.codice.ddf.admin.query.dev.system.dependency;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.karaf.bundle.core.BundleInfo;
import org.apache.karaf.bundle.core.BundleService;
import org.apache.karaf.bundle.core.BundleState;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public class WaitForBundles {

  private static final Logger LOGGER = LoggerFactory.getLogger(WaitForBundles.class);

  // TODO: tbatie - 4/4/18 - Write follow up ticket to refactor this out of ServiceManager in
  // test-itests-common
  public static final long FEATURES_AND_BUNDLES_TIMEOUT = TimeUnit.MINUTES.toMillis(10);

  private static final Map<Integer, String> BUNDLE_STATES =
          new ImmutableMap.Builder<Integer, String>()
                  .put(Bundle.UNINSTALLED, "UNINSTALLED")
                  .put(Bundle.INSTALLED, "INSTALLED")
                  .put(Bundle.RESOLVED, "RESOLVED")
                  .put(Bundle.STARTING, "STARTING")
                  .put(Bundle.STOPPING, "STOPPING")
                  .put(Bundle.ACTIVE, "ACTIVE")
                  .build();

  public static void waitForAllBundles() throws InterruptedException {
    waitForRequiredBundles("");
  }

  public static void waitForRequiredBundles(String symbolicNamePrefix) throws InterruptedException {
    boolean ready = false;
    BundleService bundleService = getService(BundleService.class);

    long timeoutLimit = System.currentTimeMillis() + FEATURES_AND_BUNDLES_TIMEOUT;
    while (!ready) {
      List<Bundle> bundles = Arrays.asList(getBundleContext().getBundles());

      ready = true;
      for (Bundle bundle : bundles) {
        if (bundle.getSymbolicName().startsWith(symbolicNamePrefix)) {
          String bundleName = bundle.getHeaders().get(Constants.BUNDLE_NAME);
          BundleInfo bundleInfo = bundleService.getInfo(bundle);
          BundleState bundleState = bundleInfo.getState();
          if (bundleInfo.isFragment()) {
            if (!BundleState.Resolved.equals(bundleState)) {
              LOGGER.info(
                      "{} bundle not ready yet.\n{}", bundleName, bundleService.getDiag(bundle));
              ready = false;
            }
          } else if (bundleState != null) {
            if (BundleState.Failure.equals(bundleState)) {
            } else if (!BundleState.Active.equals(bundleState)) {
              LOGGER.info(
                      "{} bundle not ready with state {}\n{}",
                      bundleName,
                      bundleState,
                      bundleService.getDiag(bundle));
              ready = false;
            }
          }
        }
      }

      if (!ready) {
        if (System.currentTimeMillis() > timeoutLimit) {
        }
        Thread.sleep(1000);
      }
    }
  }

  public static <S> S getService(Class<S> aClass) {
    return getService(getBundleContext().getServiceReference(aClass));
  }

  public static <S> S getService(ServiceReference<S> serviceReference) {
    return getBundleContext().getService(serviceReference);
  }

  public static <S> ServiceReference<S> getServiceReference(Class<S> aClass) {
    return getBundleContext().getServiceReference(aClass);
  }

  public static BundleContext getBundleContext() {
    Bundle bundle = FrameworkUtil.getBundle(WaitForBundles.class);
    if (bundle != null) {
      return bundle.getBundleContext();
    }
    return null;
  }
}
