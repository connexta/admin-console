package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.commons.fields.base.EnumField;
import org.codice.ddf.admin.query.commons.fields.base.EnumFieldValue;

import com.google.common.collect.ImmutableList;

public class LdapEncryptionMethodField extends EnumField<String> {

  public static final String FIELD_NAME = "encryption";
  public static final String FIELD_TYPE_NAME = "EncryptionMethod";
  public static final String DESCRIPTION = "All possible encryption methods supported to establish an LDAP connection.";
  public static final EnumFieldValue NONE = new EnumFieldValue<>("none", "none", "No encryption enabled for LDAP connection");
  public static final EnumFieldValue LDAPS = new EnumFieldValue<>("ldaps", "LDAPS", "Secure LDAPS encryption.");
  public static final EnumFieldValue START_TLS = new EnumFieldValue<>("startTls", "START_TLS", "Attempts to upgrade a non encrypted connection to LDAPS.");

  private static final List<EnumFieldValue<String>> ENCRYPTION_METHODS = ImmutableList.of(NONE, LDAPS, START_TLS);

  public LdapEncryptionMethodField() {
    super(FIELD_NAME, FIELD_TYPE_NAME);
  }

  @Override
  public String description() {
      return DESCRIPTION;
  }

  @Override
  public List<EnumFieldValue<String>> getEnumValues() {
    return ENCRYPTION_METHODS;
  }
}
