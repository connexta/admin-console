package org.codice.ddf.admin.query.ldap.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapEncryptionMethodField extends BaseEnumField<String> {

    public static final String FIELD_NAME = "encryption";
    public static final String FIELD_TYPE_NAME = "EncryptionMethod";
    public static final String DESCRIPTION = "All possible encryption methods supported to establish an LDAP connection.";

    public static final LdapEncryptionMethodField NO_ENCRYPTION = new LdapEncryptionMethodField(new NoEncryption());
    public static final LdapEncryptionMethodField LDAPS_ENCRYPTION = new LdapEncryptionMethodField(new LdapsEncryption());
    public static final LdapEncryptionMethodField START_TLS = new LdapEncryptionMethodField(new StartTlsEncryption());

    public LdapEncryptionMethodField() {
        this(null);
    }

    protected LdapEncryptionMethodField(Field<String> encryptionMethod) {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION, ImmutableList.of(new NoEncryption(), new LdapsEncryption(), new StartTlsEncryption()), encryptionMethod);
    }

    protected static final class NoEncryption extends StringField {
        public static final String NONE = "none";

        public static final String DESCRIPTION = "No encryption enabled for LDAP connection";

        public NoEncryption() {
            super(NONE, NONE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return NONE;
        }
    }

    protected static final class LdapsEncryption extends StringField {
        public static final String LDAPS = "ldaps";

        public static final String DESCRIPTION = "Secure LDAPS encryption.";

        public LdapsEncryption() {
            super(LDAPS, LDAPS, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return LDAPS;
        }
    }

    protected static final class StartTlsEncryption extends StringField {
        public static final String START_TLS = "startTls";

        public static final String DESCRIPTION =
                "Attempts to upgrade a non encrypted connection to LDAPS.";

        public StartTlsEncryption() {
            super(START_TLS, START_TLS, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return START_TLS;
        }
    }

}
