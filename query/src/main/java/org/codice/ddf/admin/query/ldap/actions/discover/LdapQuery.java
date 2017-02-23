package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.DefaultAction;
import org.codice.ddf.admin.query.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapEntriesListField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapEntryField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapQueryField;

import com.google.common.collect.ImmutableList;

public class LdapQuery extends DefaultAction<LdapEntriesListField>{

    //	entries(baseDn: !String, query: LdapQuery): [LdapEntry]

    public static final String ACTION_ID = "query";
    public static final String DESCRIPTION = "Executes a query against LDAP.";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapDistinguishedName(), new LdapQueryField());

    public LdapQuery() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null, new LdapEntriesListField());
    }

    @Override
    public LdapEntriesListField process(Map args) {
        LdapAttributeField attri = new LdapAttributeField().setValue("exampleAttri");
        LdapEntryField entry = new LdapEntryField().addAttribute(attri);
        LdapEntryField outterEntry = new LdapEntryField().addAttribute(attri)
                .addEntry(entry);
        return new LdapEntriesListField().addField(outterEntry);
    }
}
