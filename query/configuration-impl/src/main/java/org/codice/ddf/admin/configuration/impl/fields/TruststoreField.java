package org.codice.ddf.admin.configuration.impl.fields;

import com.google.common.collect.ImmutableList;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.common.PasswordField;
import org.codice.ddf.admin.configuration.api.security.TruststoreConfiguration;
import org.codice.ddf.admin.configuration.impl.TruststoreConfigurationImpl;

public class TruststoreField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "truststore";

  public static final String FIELD_TYPE_NAME = "Truststore";

  public static final String DESCRIPTION =
      "A list of of remote connections that should be trusted or not";

  public static final String FILE = "truststoreFile";

  private PasswordField password;

  private EncodedString truststoreFile;

  public TruststoreField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);

    password = new PasswordField();
    truststoreFile = new EncodedString(FILE);
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(password, truststoreFile);
  }

  public TruststoreConfiguration toConfig() {
    // TODO: tbatie - 3/2/18 - this validation should be do before reaching this
    byte[] file;

    try {
      file = truststoreFile.string().getBytes(truststoreFile.encoding());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    return new TruststoreConfigurationImpl(password.getValue(), file);
  }
}
