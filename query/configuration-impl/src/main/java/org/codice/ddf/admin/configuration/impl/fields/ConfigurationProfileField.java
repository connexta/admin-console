package org.codice.ddf.admin.configuration.impl.fields;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.Callable;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.configuration.api.DefaultConfigurationProfile;
import org.codice.ddf.admin.security.common.fields.sts.StsClaimField;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;

public class ConfigurationProfileField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "installationProfile";
  public static final String FIELD_TYPE_NAME = "InstallationProfile";
  public static final String DESCRIPTION =
      "Describes a preset of configurations to be installed in the system.";

  private static final String PROFILE_ID = "id";
  private static final String PROFILE_NAME = "name";
  private static final String PROFILE_DESCRIPTION = "description";

  public static final String GUEST_CLAIMS = "guestClaims";
  public static final String STS_CLAIMS = "stsClaims";

  private StringField profileId;
  private StringField profileName;
  private StringField profileDescription;

  private ClaimsMapEntry.ListImpl guestClaims;
  private TemporaryUser.ListImpl tempUsers;
  private SystemBannerField sysBanner;
  // TODO: tbatie - 3/8/18 System information config
  private StsClaimField.ListImpl stsClaims;

  public ConfigurationProfileField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    profileId = new StringField(PROFILE_ID);
    profileName = new StringField(PROFILE_NAME);
    profileDescription = new StringField(PROFILE_DESCRIPTION);

    guestClaims = new ClaimsMapEntry.ListImpl(GUEST_CLAIMS);
    tempUsers = new TemporaryUser.ListImpl();
    sysBanner = new SystemBannerField();
    stsClaims = new StsClaimField.ListImpl(STS_CLAIMS);
  }

  public ConfigurationProfileField(DefaultConfigurationProfile installationProfile) {
    this();
    profileId.setValue(installationProfile.getId());
    profileName.setValue(installationProfile.getName());
    profileDescription.setValue(installationProfile.getDescription());

    guestClaims.fromMap(installationProfile.getGuestClaims().getClaimsMapping());
    tempUsers.fromConfigs(installationProfile.getTemporaryUsers());
    sysBanner.fromConfig(installationProfile.getSystemBannerConfiguration());
    stsClaims.setValue(installationProfile.getStsConfiguration().getClaims());
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(guestClaims, tempUsers, sysBanner, stsClaims);
  }

  public static class ListImpl extends BaseListField<ConfigurationProfileField> {

    public static final String DEFAULT_FIELD_NAME = "profiles";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    public ListImpl(List<DefaultConfigurationProfile> profiles) {
      this();
      profiles.stream().map(ConfigurationProfileField::new).forEach(this::add);
    }

    @Override
    public Callable<ConfigurationProfileField> getCreateListEntryCallable() {
      return ConfigurationProfileField::new;
    }
  }
}
