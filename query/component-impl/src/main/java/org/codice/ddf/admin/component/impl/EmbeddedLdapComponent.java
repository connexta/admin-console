package org.codice.ddf.admin.component.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.apache.karaf.features.FeaturesService;

public class EmbeddedLdapComponent extends FeatureBasedComponent {

  public static final String ID = "embedded_ldap_component";

  public static final String DESCRIPTION =
      "Starts a LDAP testing server full of fake users. Not to be used in production.";

  public static final Set<String> FEATURES_TO_START =
      ImmutableSet.of("opendj-embedded", "ldap-embedded-default-configs");

  public EmbeddedLdapComponent(FeaturesService featuresService) {
    super(ID, DESCRIPTION, FEATURES_TO_START, featuresService);
  }
}
