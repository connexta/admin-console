package org.codice.ddf.admin.component.impl.persist;

import static org.codice.ddf.admin.component.impl.common.Messages.COMPONENT_NOT_FOUND;
import static org.codice.ddf.admin.component.impl.common.Messages.FAILED_TO_START_COMPONENT;
import static org.codice.ddf.admin.component.impl.common.Messages.failedToStartComponent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.component.api.Component;
import org.codice.ddf.admin.component.impl.common.ComponentUtils;

public class StartComponent extends BaseFunctionField<BooleanField> {

  public static final String FUNCTION_NAME = "startComponent";

  public static final String DESCRIPTION = "Starts the specified component";

  public static final String COMPONENT_ID = "componentId";

  private static final BooleanField RETURN_TYPE = new BooleanField();

  private StringField componentIdArg;

  private List<Component> components;

  private ComponentUtils componentUtils;

  public StartComponent(List<Component> components) {
    super(FUNCTION_NAME, DESCRIPTION);
    componentIdArg = new StringField(COMPONENT_ID);

    this.components = components;
    componentUtils = new ComponentUtils();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(componentIdArg);
  }

  @Override
  public BooleanField performFunction() {
    Report<Component> matchedComponent = componentUtils.matchComponent(components, componentIdArg);
    addErrorMessages(matchedComponent);

    if (containsErrorMsgs()) {
      return BooleanField.of(false);
    }

    boolean startedSuccessfully = matchedComponent.getResult().start();
    if (!startedSuccessfully) {
      addErrorMessage(failedToStartComponent(getPath()));
    }

    return BooleanField.of(startedSuccessfully);
  }

  @Override
  public BooleanField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public StartComponent newInstance() {
    return new StartComponent(components);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(COMPONENT_NOT_FOUND, FAILED_TO_START_COMPONENT);
  }
}
