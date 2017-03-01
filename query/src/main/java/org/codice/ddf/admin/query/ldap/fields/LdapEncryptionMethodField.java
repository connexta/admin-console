package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapEncryptionMethodField extends BaseEnumField<StringField> {

    public static final String FIELD_NAME = "encryption";

    public static final String FIELD_TYPE_NAME = "EncryptionMethod";

    public static final String DESCRIPTION =
            "All possible encryption methods supported to establish an LDAP connection.";

    public static final StringField NO_ENCRYPTION = new NoEncryption();

    public static final StringField LDAPS_ENCRYPTION = new LdapsEncryption();

    public static final StringField START_TLS = new StartTlsEncryption();

    public LdapEncryptionMethodField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public String getValue() {
        return (String) super.getValue();
    }

    @Override
    public List<StringField> getEnumValues() {
        return ImmutableList.of(NO_ENCRYPTION, LDAPS_ENCRYPTION, START_TLS);
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
