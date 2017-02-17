package org.codice.ddf.admin.query.ldap.actions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.api.field.Message;
import org.codice.ddf.admin.query.api.field.Report;
import org.codice.ddf.admin.query.commons.action.TestAction;
import org.codice.ddf.admin.query.commons.field.BaseFields;
import org.codice.ddf.admin.query.ldap.LdapFields;

import com.google.common.collect.ImmutableList;

public class LdapTestSettings extends TestAction {
    public static final String ACTION_ID = "testSettings";
    public static final String DESCRIPTION = "Tests whether the given LDAP dn's and user attributes exist.";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapFields.LdapSettings());

    public LdapTestSettings() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null);
    }

    @Override
    public Report process(Map<String, Object> args) {
        Message successMsg = new BaseFields.SuccessMessage("SUCCESS", "All fields have been successfully validated.");
        Message warningMsg = new BaseFields.WarningMessage("WARNING", "No users in baseDN with the given attributes");
        Message failureMsg = new BaseFields.FailureMessage("CANNOT_BIND", "The specified user DN does not exist.");
        return new BaseFields.BaseReport().messages(successMsg, warningMsg, failureMsg);
    }
}
