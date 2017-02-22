package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.StringField;

import com.google.common.collect.ImmutableList;

public class LdapSettingsField extends ObjectField {

    public static final String FIELD_NAME = "settings";
    public static final String FIELD_TYPE_NAME = "LdapSettings";
    public static final String DESCRIPTION = "Contains information about the LDAP structure and various attributes required to setup.";

    // TODO: tbatie - 2/16/17 - Need to assess these fields again, FIELD class contains a T value that should be used instead
    private String userBaseDn;
    private String groupBaseDn;
    private String groupObjectCLass;
    private String groupMembershipAttribute;

    public static final List<Field> FIELDS = ImmutableList.of(new StringField("userNameAttribute"),
            new LdapDn("userBaseDn"),
            new LdapDn("groupBaseDn"),
            new StringField("groupObjectClass"),
            new StringField("groupMembershipAttribute"),
            new LdapAttributeMappingField());

    public LdapSettingsField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
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
