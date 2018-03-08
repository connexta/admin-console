package org.codice.ddf.admin.configuration.impl.fields;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.PasswordField;
import org.codice.ddf.admin.configuration.api.security.TemporaryUserConfiguration;
import org.codice.ddf.admin.configuration.impl.UserConfigurationImpl;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;

public class TemporaryUser extends BaseObjectField {

  private static final String DEFAULT_FIELD_NAME = "tempUser";

  private static final String FIELD_TYPE_NAME = "TemporaryUser";

  private static final String DESCRIPTION =
      "A temporary user that is able to log into the system. This is for installation/testing purposes only and should not be used in production.";

  private StringField username;

  private PasswordField password;

  private StringField.ListImpl roles;

  private ClaimsMapEntry.ListImpl claimsMapping;

  // TODO: tbatie - 3/1/18 - Add the claims mapping
  public TemporaryUser() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    this.username = new StringField("username");
    this.password = new PasswordField();
    this.roles = new StringField.ListImpl();
    this.claimsMapping = new ClaimsMapEntry.ListImpl();
  }

  public TemporaryUser(TemporaryUserConfiguration user) {
    this();
    username.setValue(user.getUsername());
    password.setValue(user.getPassword());
    roles.setValue(user.getRoles());
    claimsMapping.fromMap(user.getClaimsMapping());
  }

  public TemporaryUser useDefaultRequired() {
    username.isRequired(true);
    password.isRequired(true);
    roles.isRequired(true);
    claimsMapping.useDefaultRequired();
    return this;
  }

  public TemporaryUserConfiguration toConfig() {
    return new UserConfigurationImpl(
        username.getValue(), password.getValue(), roles.getValue(), new HashMap<>());
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(username, password, roles);
  }

  public static class ListImpl extends BaseListField<TemporaryUser> {

    public static final String DEFAULT_FIELD_NAME = "tempUsers";

    private Callable<TemporaryUser> createTemporaryUser;

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
      createTemporaryUser = TemporaryUser::new;
    }

    public List<TemporaryUserConfiguration> toConfigs() {
      return getList().stream().map(TemporaryUser::toConfig).collect(Collectors.toList());
    }

    @Override
    public ListImpl useDefaultRequired() {
      createTemporaryUser = () -> new TemporaryUser().useDefaultRequired();
      isRequired(true);
      return this;
    }

    @Override
    public Callable<TemporaryUser> getCreateListEntryCallable() {
      return createTemporaryUser;
    }

    public void fromConfigs(List<TemporaryUserConfiguration> users) {
      users.stream().map(TemporaryUser::new).forEach(this::add);
    }
  }
}
