package org.codice.ddf.admin.query.ldap;

import java.util.List;

import org.codice.ddf.admin.query.api.field.ActionHandlerFields;
import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.field.BaseFields;
import org.codice.ddf.admin.query.commons.CommonFields;

import com.google.common.collect.ImmutableList;

public class LdapFields implements ActionHandlerFields {
    @Override
    public List<Field> allFields() {
        return null;
    }

    public static class LdapEncryptionMethod extends BaseFields.EnumField<String> {
        public static final String ENCRYPTION_METHOD = "EncryptionMethod";

        public static final BaseFields.EnumValue NONE = new BaseFields.EnumValue<>("none", "none", "No encryption enabled for LDAP connection");
        public static final BaseFields.EnumValue LDAPS = new BaseFields.EnumValue<>("ldaps", "LDAPS", "Secure LDAPS encryption.");
        public static final BaseFields.EnumValue START_TLS = new BaseFields.EnumValue<>("startTls", "START_TLS", "Attempts to upgrade a non encrypted connection to LDAPS.");
        private static final List<BaseFields.EnumValue<String>> ENCRYPTION_METHODS = ImmutableList.of(NONE, LDAPS, START_TLS);

        public LdapEncryptionMethod() {
            super("encryption", ENCRYPTION_METHOD);
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
        public static final String LDAP_CONNECTION = "LdapConnection";
        public static final List<Field> FIELDS = ImmutableList.of(new CommonFields.HostnameField(), new CommonFields.PortField(), new LdapEncryptionMethod());
        public LdapConnection() {
            super("connection", LDAP_CONNECTION);
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
        public static final String LDAP_CREDENTIALS = "LdapCredentials";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";

        public static final List<Field> FIELDS = ImmutableList.of(new BaseFields.StringField(USERNAME), new BaseFields.StringField(PASSWORD));

        public LdapCredentials() {
            super("credentials", LDAP_CREDENTIALS);
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

        public static final String ATTRIBUTE_MAPPING  = "AttributeMapping";
        private List<Field> FIELDS = ImmutableList.of(new BaseFields.StringField("stsClaim"), new BaseFields.StringField("userAttribute"));

        public LdapAttributeMap() {
            super("attributeMap", ATTRIBUTE_MAPPING);
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

    public static class LdapAttributeMappings extends BaseFields.ListField {

        public static final String MAPPING = "mapping";

        public LdapAttributeMappings() {
            super(MAPPING);
        }

        @Override
        public String description() {
            return "A map containing STS claims to user attributes. Only 1 sts claim is allowed to be mapped to a single user attribute.";
        }

        @Override
        public Field getListValueField() {
            return new LdapAttributeMap();
        }
    }


    public static final class LdapSettings extends BaseFields.ObjectField {

        public static final String LDAP_SETTINGS = "LdapSettings";

        // TODO: tbatie - 2/16/17 - Need to assess these fields again, FIELD class contains a T value that should be used instead
        private String userBaseDn;
        private String groupBaseDn;
        private String groupObjectCLass;
        private String groupMembershipAttribute;

        public static final List<Field> FIELDS = ImmutableList.of(new BaseFields.StringField("userNameAttribute"),
                new BaseFields.StringField("userBaseDn"),
                new BaseFields.StringField("groupBaseDn"),
                new BaseFields.StringField("groupObjectClass"),
                new BaseFields.StringField("groupMembershipAttribute"),
                new LdapAttributeMappings());

        public LdapSettings() {
            super("settings", LDAP_SETTINGS);
        }

        @Override
        public String description() {
            return "Contains information about the LDAP structure and various attributes required to setup.";
        }

        @Override
        public List<Field> getFields() {
            return FIELDS;
        }

        public String getUserBaseDn() {
            return userBaseDn;
        }

        public String getGroupBaseDn() {
            return groupBaseDn;
        }

        public String getGroupObjectClass() {
            return groupObjectCLass;
        }

        public String getGroupMembershipAttribute() {
            return groupMembershipAttribute;
        }

        public void setUserBaseDn(String userBaseDn) {
            this.userBaseDn = userBaseDn;
        }

        public void setGroupBaseDn(String groupBaseDn) {
            this.groupBaseDn = groupBaseDn;
        }

        public void setGroupObjectClass(String groupObjectCLass) {
            this.groupObjectCLass = groupObjectCLass;
        }

        public void setGroupMembershipAttribute(String groupMembershipAttribute) {
            this.groupMembershipAttribute = groupMembershipAttribute;
        }
    }

    public static final class LdapConfiguration extends BaseFields.ObjectField {

        public static final String LDAP_CONFIGURATION = "LdapConfiguration";
        public static final List<Field> FIELDS = ImmutableList.of(new BaseFields.Pid(),
                new LdapConnection(),
                new LdapCredentials(),
                new LdapSettings());

        public LdapConfiguration() {
            super("config", LDAP_CONFIGURATION);
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
