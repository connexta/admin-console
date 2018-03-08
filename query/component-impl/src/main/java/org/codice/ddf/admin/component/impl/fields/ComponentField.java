package org.codice.ddf.admin.component.impl.fields;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.Callable;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.component.api.Component;

public class ComponentField extends BaseObjectField {

  public static final String FIELD_TYPE = "Component";

  public static final String DEFAULT_FIELD_NAME = "component";

  public static final String DESCRIPTION = "Defines a type of functionality that can be started";

  private StringField id;
  private StringField description;

  public ComponentField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE, DESCRIPTION);
    id = new StringField("id");
    description = new StringField("description");
  }

  public ComponentField(Component component) {
    this();
    id.setValue(component.getId());
    description.setValue(component.getDescription());
  }

  public String id() {
    return id.getValue();
  }

  public String description() {
    return description.getValue();
  }

  public StringField getDescriptionField() {
    return description;
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(id, description);
  }

  public static class ListImpl extends BaseListField<ComponentField> {

    public static final String DEFAULT_FIELD_NAME = "components";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    public ListImpl(List<Component> components) {
      this();
      components.stream().map(ComponentField::new).forEach(this::add);
    }

    @Override
    public Callable<ComponentField> getCreateListEntryCallable() {
      return ComponentField::new;
    }
  }
}
