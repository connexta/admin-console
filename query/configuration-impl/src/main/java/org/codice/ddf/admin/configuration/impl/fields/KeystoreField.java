package org.codice.ddf.admin.configuration.impl.fields;

import com.google.common.collect.ImmutableList;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.common.PasswordField;
import org.codice.ddf.admin.configuration.api.security.KeystoreConfiguration;
import org.codice.ddf.admin.configuration.impl.KeystoreConfigurationImpl;

public class KeystoreField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "keystore";
  public static final String FIELD_TYPE_NAME = "Keystore";
  public static final String DESCRIPTION =
      "Represents a storage facility for cryptographic keys and certificates";

  private static final String FILE = "file";

  public static final String KEYSTORE_PASS = "keystorePassword";

  public static final String PRIVATE_KEY_PASS = "privateKeyPassword";

  private PasswordField keystorePassword;
  private PasswordField privateKeyPassword;
  private EncodedString keystoreFile;

  public KeystoreField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    keystorePassword = new PasswordField(KEYSTORE_PASS);
    privateKeyPassword = new PasswordField(PRIVATE_KEY_PASS);
    keystoreFile = new EncodedString(FILE);
  }

  public KeystoreConfiguration toKeystoreConfiguration() {
    try {
      byte[] file = keystoreFile.string().getBytes(keystoreFile.encoding());
      return new KeystoreConfigurationImpl(
          keystorePassword.getValue(), privateKeyPassword.getValue(), file);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  // TODO: tbatie - 3/2/18 - Validation should make sure decoding can take place
  @Override
  public List<Field> getFields() {
    return ImmutableList.of(keystorePassword, privateKeyPassword, keystoreFile);
  }
}
