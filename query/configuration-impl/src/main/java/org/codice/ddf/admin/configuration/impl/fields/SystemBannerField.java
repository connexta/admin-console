package org.codice.ddf.admin.configuration.impl.fields;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.configuration.api.theme.SystemBannerConfiguration;
import org.codice.ddf.admin.configuration.impl.SystemBannerConfigurationImpl;

public class SystemBannerField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "systemBanner";

  public static final String FIELD_TYPE_NAME = "SystemBanner";

  public static final String DESCRIPTION =
      "Contains textual and themeing information about the system banner.";

  public static final String SYSTEM_USAGE_TITLE = "systemUsageTitle";

  private StringField systemUsageTitle;

  // TODO: tbatie - 3/2/18 - Wire up remaining fields
  public SystemBannerField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    systemUsageTitle = new StringField(SYSTEM_USAGE_TITLE);
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(systemUsageTitle);
  }

  public void fromConfig(SystemBannerConfiguration config) {
    // TODO: tbatie - 3/8/18 - set remaining fields once written
    systemUsageTitle.setValue(config.getSystemUsageTitle());
  }

  public SystemBannerConfiguration toConfig() {
    return new SystemBannerConfigurationImpl();
  }
}
