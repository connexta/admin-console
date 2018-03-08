package org.codice.ddf.admin.component.impl;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.component.api.Component;
import org.codice.ddf.admin.component.impl.discover.GetComponent;
import org.codice.ddf.admin.component.impl.persist.StartComponent;

public class ComponentFieldProvider extends BaseFieldProvider {

  public static final String FIELD_NAME = "components";
  public static final String TYPE_NAME = "CapabilitiesOperations";
  public static final String DESCRIPTION =
      "Various operations for testing and retrieving existing Capabilities";

  private GetComponent getCapabilities;

  private StartComponent startComponent;

  // TODO: tbatie - 2/21/18 - Do we really need a provider for this? Could the graphql schema get a
  // reference to DiscoveryFunctions and MutationFunctions instead?
  public ComponentFieldProvider(List<Component> capabilities) {
    super(FIELD_NAME, TYPE_NAME, DESCRIPTION);
    getCapabilities = new GetComponent(capabilities);

    startComponent = new StartComponent(capabilities);
  }

  @Override
  public List<FunctionField> getDiscoveryFunctions() {
    return ImmutableList.of(getCapabilities);
  }

  @Override
  public List<FunctionField> getMutationFunctions() {
    return ImmutableList.of(startComponent);
  }
}
