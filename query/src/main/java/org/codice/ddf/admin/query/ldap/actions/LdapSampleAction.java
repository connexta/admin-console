package org.codice.ddf.admin.query.ldap.actions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.api.field.Report;
import org.codice.ddf.admin.query.commons.action.TestAction;
import org.codice.ddf.admin.query.ldap.LdapFields;

import com.google.common.collect.ImmutableList;

public class LdapSampleAction extends TestAction {

    public static final String ACTION_ID = "test";
    public static final String DESCRIPTION = "Remove me plz";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapFields.LdapConfiguration());

    public LdapSampleAction() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null);
    }

    @Override
    public Report process(Map<String, Object> args) {
        return null;
    }
}
