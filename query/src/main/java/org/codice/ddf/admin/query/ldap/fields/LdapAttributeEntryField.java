package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.StringField;

import com.google.common.collect.ImmutableList;

public class LdapAttributeEntryField extends ObjectField {

    public static final String FIELD_NAME = "attributeMapping";
    public static final String STS_CLAIM = "stsClaim";
    public static final String USER_ATTRIBUTE = "userAttribute";

    private List<Field> FIELDS = ImmutableList.of(new StringField(STS_CLAIM), new StringField(USER_ATTRIBUTE));

    public LdapAttributeEntryField() {
        super(FIELD_NAME, FIELD_NAME);
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
