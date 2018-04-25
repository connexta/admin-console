package org.codice.ddf.admin.query.dev.system.graph;

import static org.codice.ddf.admin.query.dev.system.dependency.BundleUtils.STATE_TO_VALUE;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.FloatField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils;

import com.google.common.collect.ImmutableList;

public class BundleStateField extends BaseObjectField {

  public static final String FIELD_TYPE = "BundleState";

  public static final String DEFAULT_FIELD_NAME = "bundleState";

  public static final String DESCRIPTION = "A period of time a bundle is in a particular state.";

  public static final String LOCATION = "location";

  public static final String STATE = "state";

  public static final String START_OF_STATE = "startOfState";

  public static final String END_OF_STATE = "endOfState";

  private StringField bundleLocation;

  private StringField bundleState;

  private FloatField startOfState;

  private FloatField endOfState;

  public BundleStateField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE, DESCRIPTION);
    this.bundleLocation = new StringField(LOCATION);
    this.bundleState = new StringField(STATE);
    this.startOfState = new FloatField(START_OF_STATE);
    this.endOfState = new FloatField(END_OF_STATE);
  }

  public BundleStateField(String location, int state, long startTime) {
    this();
    bundleLocation.setValue(location);
    bundleState.setValue(BundleUtils.STATE_TO_NAME_MAP.get(state));
    startOfState.setValue((float) startTime);
  }

  public int state() {
    return STATE_TO_VALUE.get(bundleState.getValue());
  }

  public void endTime(long endTime) {
    endOfState.setValue((float) endTime);
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(bundleLocation, bundleState, startOfState, endOfState);
  }

  public static class ListImpl extends BaseListField<BundleStateField> {

    public static final String DEFAULT_FIELD_NAME = "bundleStates";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    public ListImpl(String fieldName) {
      super(fieldName);
    }

    @Override
    public Callable<BundleStateField> getCreateListEntryCallable() {
      return BundleStateField::new;
    }

    @Override
    public BundleStateField.ListImpl addAll(Collection<BundleStateField> values) {
      super.addAll(values);
      return this;
    }
  }
}
