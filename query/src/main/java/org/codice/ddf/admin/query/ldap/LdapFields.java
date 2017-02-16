package org.codice.ddf.admin.query.ldap;

import java.util.List;

import org.codice.ddf.admin.query.api.field.ActionHandlerFields;
import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.field.BaseFields;
import org.codice.ddf.admin.query.commons.CommonFields;

import com.google.common.collect.ImmutableList;

public class LdapFields implements ActionHandlerFields{
    @Override
    public List<Field> allFields() {
        return null;
    }

    public static class LdapEncryptionMethod extends BaseFields.EnumField<String> {
        public static final String ENCRYPTION_METHOD = "encryptionMethod";

        public static final BaseFields.EnumValue NONE = new BaseFields.EnumValue<>("none", "none", "No encryption enabled for LDAP connection");
        public static final BaseFields.EnumValue LDAPS = new BaseFields.EnumValue<>("ldaps", "LDAPS", "Secure LDAPS encryption.");
        public static final BaseFields.EnumValue START_TLS = new BaseFields.EnumValue<>("startTls", "START_TLS", "Attempts to upgrade a non encrypted connection to LDAPS.");
        private static final List<BaseFields.EnumValue<String>> ENCRYPTION_METHODS = ImmutableList.of(NONE, LDAPS, START_TLS);

        public LdapEncryptionMethod() {
            super(ENCRYPTION_METHOD);
        }

        @Override
        public String description() {
            return "All possible encryption methods supported to establish an LDAP connection.";
        }


        @Override
        public List<BaseFields.EnumValue<String>> getEnumValues() {
            return ENCRYPTION_METHODS;
        }

    }

    public static class LdapConnection extends BaseFields.ObjectField {
        public static final String LDAP_CONNECTION = "ldapConnection";
        public static final List<Field> FIELDS = ImmutableList.of(new CommonFields.HostnameField(), new CommonFields.PortField(), new LdapEncryptionMethod());
        public LdapConnection() {
            super(LDAP_CONNECTION);
        }

        @Override
        public String description() {
            return "Contains the required information to establish an LDAP connection.";
        }

        @Override
        public List<Field> getFields() {
            return FIELDS;
        }
    }

    public static class LdapCredentials extends BaseFields.ObjectField {
        public static final String LDAP_CREDENTIALS = "ldapCredentials";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";

        public static final List<Field> FIELDS = ImmutableList.of(new BaseFields.StringField(USERNAME), new BaseFields.StringField(PASSWORD));

        public LdapCredentials() {
            super(LDAP_CREDENTIALS);
        }

        @Override
        public String description() {
            return "Contains the required credentials to bind a user to an LDAP connection.";
        }

        @Override
        public List<Field> getFields() {
            return FIELDS;
        }
    }

    public static class LdapAttributeMap extends BaseFields.ObjectField {

        public static final String ATTRIBUTE_MAPPING  = "attributeMapping";
        private List<Field> FIELDS = ImmutableList.of(new BaseFields.StringField("stsClaim"), new BaseFields.StringField("userAttribute"));

        public LdapAttributeMap() {
            super(ATTRIBUTE_MAPPING);
        }

        @Override
        public String description() {
            return "A mapping from an STS claim to a user attribute.";
        }

        @Override
        public List<Field> getFields() {
            return FIELDS;
        }
    }

    public static class LdapAttributeMappings extends BaseFields.List {

        public static final String MAPPING = "mapping";

        public LdapAttributeMappings() {
            super(MAPPING);
        }

        @Override
        public String description() {
            return "A map containing STS claim to user attribute mapping";
        }

        @Override
        public Field getListValueField() {
            return new LdapAttributeMap();
        }
    }


    public static final class LdapSettings extends BaseFields.ObjectField {

        public static final String LDAP_SETTINGS = "ldapSettings";

        public static final List<Field> FIELDS = ImmutableList.of(new BaseFields.StringField("userNameAttribute"),
                new BaseFields.StringField("userBaseDn"),
                new BaseFields.StringField("groupBaseDn"),
                new BaseFields.StringField("groupObjectClass"),
                new BaseFields.StringField("groupMemberShipAttribute"),
                new LdapAttributeMappings());

        public LdapSettings() {
            super(LDAP_SETTINGS);
        }

        @Override
        public String description() {
            return "Contains information about the LDAP structure and various attributes required to setup.";
        }

        @Override
        public List<Field> getFields() {
            return FIELDS;
        }
    }

    public static final class LdapConfiguration extends BaseFields.ObjectField {

        public static final String LDAP_CONFIGURATION = "ldapConfiguration";
        public static final List<Field> FIELDS = ImmutableList.of(new BaseFields.Pid(),
                new LdapConnection(),
                new LdapCredentials(),
                new LdapSettings());

        public LdapConfiguration() {
            super(LDAP_CONFIGURATION);
        }

        @Override
        public String description() {
            return "A configuration containing all the required fields for saving LDAP settings";
        }

        @Override
        public List<Field> getFields() {
            return FIELDS;
        }
    }
}
