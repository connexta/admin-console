package org.codice.ddf.admin.component.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.apache.karaf.features.FeaturesService;

public class CswSourceComponent extends FeatureBasedComponent {

  public static final String ID = "csw_source_component";

  public static final String DESCRIPTION =
      "Allows for creation of Catalog Service for the Web (CSW) sources . CSW sources can be used to federate to other system to retrieve metadata.";

  public static final Set<String> FEATURES_TO_START = ImmutableSet.of("spatial-csw");

  public CswSourceComponent(FeaturesService featuresService) {
    super(ID, DESCRIPTION, FEATURES_TO_START, featuresService);
  }
}
