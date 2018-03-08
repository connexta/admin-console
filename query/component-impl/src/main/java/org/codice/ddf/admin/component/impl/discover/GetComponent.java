package org.codice.ddf.admin.component.impl.discover;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.component.api.Component;
import org.codice.ddf.admin.component.impl.fields.ComponentField;

public class GetComponent extends GetFunctionField<ComponentField.ListImpl> {

  private static final String FUNCTION_NAME = "components";
  private static final String DESCRIPTION =
      "Returns all components the system is capable of starting.";
  private List<Component> components;

  public GetComponent(List<Component> components) {
    super(FUNCTION_NAME, DESCRIPTION);
    this.components = components;
  }

  @Override
  public ComponentField.ListImpl performFunction() {
    return new ComponentField.ListImpl(components);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }

  @Override
  public ComponentField.ListImpl getReturnType() {
    return new ComponentField.ListImpl();
  }

  @Override
  public FunctionField<ComponentField.ListImpl> newInstance() {
    return new GetComponent(components);
  }
}
