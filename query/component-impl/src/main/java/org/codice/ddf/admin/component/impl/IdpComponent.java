package org.codice.ddf.admin.component.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.apache.karaf.features.FeaturesService;

public class IdpComponent extends FeatureBasedComponent {

  public static final String ID = "idp_auth_component";

  public static final String DESCRIPTION =
      "Allows an external system to provide authentication using Single Sign On (SSO).";

  public static final Set<String> FEATURES_TO_START = ImmutableSet.of("security-idp");

  public IdpComponent(FeaturesService featuresService) {
    super(ID, DESCRIPTION, FEATURES_TO_START, featuresService);
  }
}
