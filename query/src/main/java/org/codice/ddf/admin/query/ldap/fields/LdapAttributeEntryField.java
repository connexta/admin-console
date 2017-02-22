package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.StringField;

import com.google.common.collect.ImmutableList;

public class LdapAttributeEntryField extends ObjectField {

    public static final String FIELD_NAME = "attributeMapping";
    public static final String FIELD_TYPE_NAME = "AttributeMapping";
    public static final String DESCRIPTION = "A mapping from an STS claim to a user attribute.";

    public static final String STS_CLAIM = "stsClaim";
    public static final String USER_ATTRIBUTE = "userAttribute";
    private StringField stsClaim;
    private StringField userAttribute;

    public LdapAttributeEntryField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        stsClaim = new StringField(STS_CLAIM);
        userAttribute = new StringField(USER_ATTRIBUTE);
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(stsClaim, userAttribute);
    }
}
