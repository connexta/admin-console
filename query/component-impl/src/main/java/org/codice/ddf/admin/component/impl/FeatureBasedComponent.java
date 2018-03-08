package org.codice.ddf.admin.component.impl;

import java.util.EnumSet;
import java.util.Set;
import org.apache.karaf.features.FeaturesService;
import org.codice.ddf.admin.component.api.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FeatureBasedComponent implements Component {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureBasedComponent.class);

  private String id;
  private String description;
  private Set<String> featuresToStart;
  private FeaturesService featuresService;

  public FeatureBasedComponent(
      String id, String description, Set<String> featuresToStart, FeaturesService featuresService) {
    this.id = id;
    this.description = description;
    this.featuresToStart = featuresToStart;
    this.featuresService = featuresService;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean start() {
    try {
      featuresService.installFeatures(
          featuresToStart, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
    } catch (Exception e) {
      LOGGER.error("Failed to install component " + id, e);
      return false;
    }

    return true;
  }
}
