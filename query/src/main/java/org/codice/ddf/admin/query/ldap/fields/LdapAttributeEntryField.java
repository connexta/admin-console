package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;
import org.codice.ddf.admin.query.sts.field.StsClaimField;

import com.google.common.collect.ImmutableList;

public class LdapAttributeEntryField extends BaseObjectField {

    public static final String FIELD_NAME = "attributeMapping";
    public static final String FIELD_TYPE_NAME = "AttributeMapping";
    public static final String DESCRIPTION = "A mapping from an STS claim to a user setValue.";

    public static final String STS_CLAIM = "stsClaim";
    public static final String USER_ATTRIBUTE = "userAttribute";
    private StsClaimField stsClaim;
    private StringField userAttribute;

    public LdapAttributeEntryField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        stsClaim = new StsClaimField(STS_CLAIM);
        userAttribute = new StringField(USER_ATTRIBUTE);
    }

    public LdapAttributeEntryField stsClaim(String claim) {
        stsClaim.setValue(claim);
        return this;
    }

    public LdapAttributeEntryField userAttribute(String userAttribute) {
        this.userAttribute.setValue(userAttribute);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(stsClaim, userAttribute);
    }
}
