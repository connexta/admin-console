package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class Realm extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "realm";
    public static final String FIELD_TYPE_NAME = "Realm";
    public static final String DESCRIPTION = "Authenticating Realms are used to authenticate an incoming authentication token and create a Subject on successful authentication.";

    public static final Realm LDAP_REALM = new Realm(new LdapRealm());
    public static final Realm KARAF_REALM = new Realm(new KarafRealm());

    public Realm() {
        this(null);
    }

    protected Realm(Field<String> realm) {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION, ImmutableList.of(new LdapRealm(), new KarafRealm()), realm);
    }

    protected static final class LdapRealm extends StringField {
        public static final String LDAP = "ldap";

        public static final String DESCRIPTION =
                "An LDAP used for authentication of users within it's database.";

        public LdapRealm() {
            super(LDAP, LDAP, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return LDAP;
        }
    }

    protected static final class KarafRealm extends StringField {
        public static final String KARAF = "karaf";
        public static final String DESCRIPTION = "The default realm. The karaf realm authenticates against the users.properties file.";

        public KarafRealm() {
            super(KARAF, KARAF, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return KARAF;
        }
    }
}