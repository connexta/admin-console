package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapSettingsField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "settings";
    public static final String FIELD_TYPE_NAME = "LdapSettings";
    public static final String DESCRIPTION = "Contains information about the LDAP structure and various attributes required to setup.";

    // TODO: tbatie - 2/16/17 - Need to assess these fields again, FIELD class contains a T value that should be used instead
    private String USER_NAME_ATTRIBUTE = "userNameAttribute";
    private String USER_BASE_DN = "userBaseDn";
    private String GROUP_BASE_DN = "groupBaseDn";
    private String GROUP_OBJECT_CLASS = "groupObjectClass";
    private String GROUP_MEMBERSHIP_ATTRIBUTE = "groupMembershipAttribute";

    private StringField usernameAttribute;
    private LdapDistinguishedName userBaseDn;
    private LdapDistinguishedName groupBaseDn;
    private StringField groupObjectClass;
    private StringField groupMembershipAttribute;
    private LdapAttributeMappingField mapping;

    public LdapSettingsField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.usernameAttribute = new StringField(USER_NAME_ATTRIBUTE);
        this.userBaseDn = new LdapDistinguishedName(USER_BASE_DN);
        this.groupBaseDn = new LdapDistinguishedName(GROUP_BASE_DN);
        this.groupObjectClass = new StringField(GROUP_OBJECT_CLASS);
        this.groupMembershipAttribute = new StringField(GROUP_MEMBERSHIP_ATTRIBUTE);
        this.mapping = new LdapAttributeMappingField();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(usernameAttribute, userBaseDn, groupBaseDn, groupObjectClass, groupMembershipAttribute, mapping);
    }

    public String userBaseDn() {
        return userBaseDn.getValue();
    }

    public String groupBaseDn() {
        return groupBaseDn.getValue();
    }

    public String groupObjectClass() {
        return groupObjectClass.getValue();
    }

    public String groupMembershipAttribute() {
        return groupMembershipAttribute.getValue();
    }

    public String usernameAttribute() {
        return usernameAttribute.getValue();
    }

    public List<LdapAttributeEntryField> mapping() {
        return mapping.getList();
    }

    public LdapSettingsField userBaseDn(String userBaseDn) {
        this.userBaseDn.setValue(userBaseDn);
        return this;
    }

    public LdapSettingsField groupBaseDn(String groupBaseDn) {
        this.groupBaseDn.setValue(groupBaseDn);
        return this;
    }

    public LdapSettingsField groupObjectClass(String groupObjectCLass) {
        this.groupObjectClass.setValue(groupObjectCLass);
        return this;
    }

    public LdapSettingsField groupMembershipAttribute(String groupMembershipAttribute) {
        this.groupMembershipAttribute.setValue(groupMembershipAttribute);
        return this;
    }

    public LdapSettingsField usernameAttribute(String usernameAttribute) {
        this.usernameAttribute.setValue(usernameAttribute);
        return this;
    }

    public LdapSettingsField mappingEntry(String claim, String attribute) {
        mapping.add(claim, attribute);
        return this;

    }
}
