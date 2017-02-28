package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapEntriesListField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapEntryField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapQueryField;

import com.google.common.collect.ImmutableList;

public class LdapQuery extends BaseActionField<LdapEntriesListField> {

    public static final String FIELD_NAME = "query";
    public static final String DESCRIPTION = "Executes a query against LDAP.";

    private LdapDistinguishedName dn =  new LdapDistinguishedName();
    private LdapQueryField query = new LdapQueryField();
    private List<Field> arguments = ImmutableList.of(dn, query);

    public LdapQuery() {
        super(FIELD_NAME, DESCRIPTION, new LdapEntriesListField());
    }

    @Override
    public LdapEntriesListField process(Map args) {
        LdapAttributeField attri = new LdapAttributeField().setValue("exampleAttri");
        LdapEntryField entry = new LdapEntryField().addAttribute(attri);
        LdapEntryField outterEntry = new LdapEntryField().addAttribute(attri)
                .addEntry(entry);
        return new LdapEntriesListField().addField(outterEntry);
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
