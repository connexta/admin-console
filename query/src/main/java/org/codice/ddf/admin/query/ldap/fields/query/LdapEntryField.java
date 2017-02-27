package org.codice.ddf.admin.query.ldap.fields.query;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;

import com.google.common.collect.ImmutableList;

public class LdapEntryField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "entry";
    public static final String FIELD_TYPE_NAME = "LdapEntry";
    public static final String DESCRIPTION = "An entry within an LDAP server.";

    // TODO: tbatie - 2/22/17 - Can't handle recursion
//    private LdapEntriesListField entries;
    private LdapAttributeListField attributes;

    public LdapEntryField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
//        entries = new LdapEntriesListField();
        attributes = new LdapAttributeListField();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(
//                entries,
                attributes);
    }

    public LdapEntryField addEntry(LdapEntryField entry){
//        entries.addField(entry);
        return this;
    }

    public LdapEntryField addAttribute(LdapAttributeField attribute) {
        attributes.addField(attribute);
        return this;
    }
}
