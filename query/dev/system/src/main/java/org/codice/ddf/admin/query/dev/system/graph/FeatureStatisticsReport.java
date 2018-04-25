package org.codice.ddf.admin.query.dev.system.graph;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.FloatField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;

import com.google.common.collect.ImmutableList;

public class FeatureStatisticsReport extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "report";

  public static final String FIELD_TYPE_NAME = "FeatureStatisticsReport";

  public static final String DESCRIPTION = "Report containing various statistics about a feature.";

  public static final String INSTALL_BUNDLE_STATES = "installBundleStates";

  public static final String UNINSTALL_BUNDLE_STATES = "uninstallBundleStates";

  public static final String INSTALL_START_TIME = "installStartTime";

  public static final String INSTALL_FINISH_TIME = "installFinishTime";

  public static final String UNINSTALL_START_TIME = "uninstallStartTime";

  public static final String UNINSTALL_FINISH_TIME = "uninstallFinishTime";

  private BundleStateField.ListImpl installBundleStates;

  private BundleStateField.ListImpl uninstallBundleStates;

  private FloatField installStartTime;

  private FloatField installFinishTime;

  private FloatField uninstallStartTime;

  private FloatField uninstallFinishTime;

  public FeatureStatisticsReport() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    installBundleStates = new BundleStateField.ListImpl(INSTALL_BUNDLE_STATES);
    uninstallBundleStates = new BundleStateField.ListImpl(UNINSTALL_BUNDLE_STATES);
    installStartTime = new FloatField(INSTALL_START_TIME);
    installFinishTime = new FloatField(INSTALL_FINISH_TIME);
    uninstallStartTime = new FloatField(UNINSTALL_START_TIME);
    uninstallFinishTime = new FloatField(UNINSTALL_FINISH_TIME);
  }

  public void installBundleStates(List<BundleStateField> installBundleStates) {
    this.installBundleStates.addAll(installBundleStates);
  }

  public void uninstallBundleStates(List<BundleStateField> uninstallBundleStates) {
    this.uninstallBundleStates.addAll(uninstallBundleStates);
  }

  public void installStartTime(long installStartTime) {
    this.installStartTime.setValue((float) installStartTime);
  }

  public void installFinishTime(long installFinishTime) {
    this.installFinishTime.setValue((float) installFinishTime);
  }

  public void uninstallStartTime(long uninstallStartTime) {
    this.uninstallStartTime.setValue((float) uninstallStartTime);
  }

  public void uninstallFinishTime(long uninstallFinishTime) {
    this.uninstallFinishTime.setValue((float) uninstallFinishTime);
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(installBundleStates,
            uninstallBundleStates,
            installStartTime,
            installFinishTime,
            uninstallStartTime,
            uninstallFinishTime);
  }
}
