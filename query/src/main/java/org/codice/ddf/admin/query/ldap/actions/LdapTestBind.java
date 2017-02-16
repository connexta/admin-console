package org.codice.ddf.admin.query.ldap.actions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.api.field.Message;
import org.codice.ddf.admin.query.api.field.Report;
import org.codice.ddf.admin.query.commons.field.BaseFields;
import org.codice.ddf.admin.query.commons.action.TestAction;
import org.codice.ddf.admin.query.ldap.LdapFields;

import com.google.common.collect.ImmutableList;

public class LdapTestBind extends TestAction {

    public static final String ACTION_ID = "testBind";
    public static final String DESCRIPTION = "Attempts to bind a user to the given ldap connection given the ldap bind user credentials.";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapFields.LdapConnection(), new LdapFields.LdapCredentials());

    public LdapTestBind() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null);
    }

    @Override
    public Report process(Map<String, Object> args) {
        Message successMsg = new BaseFields.SuccessMessage("SUCCESS", "Able to bind user to connection.");
        Message warningMsg = new BaseFields.WarningMessage("WARNING", "Unable to bind user to connection.");
        Message failureMsg = new BaseFields.FailureMessage("CANNOT_BIND", "Failed to connect to the specified LDAP");
        return new BaseFields.BaseReport().messages(successMsg, warningMsg, failureMsg);
    }
}
