package org.codice.ddf.admin.query.ldap.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.api.field.Message;
import org.codice.ddf.admin.query.api.field.Report;
import org.codice.ddf.admin.query.commons.action.TestAction;
import org.codice.ddf.admin.query.commons.field.BaseFields;
import org.codice.ddf.admin.query.ldap.LdapFields;

public class LdapTestConnection  extends TestAction {
    public static final String ACTION_ID = "testConnect";
    public static final String ACTION_DESCRIPTION = "Attempts to established a connection with the given connection configuration";
    public static final List<Field> REQUIRED_FIELDS = Arrays.asList(new LdapFields.LdapConnection());

    public LdapTestConnection() {
        super(ACTION_ID, ACTION_DESCRIPTION, REQUIRED_FIELDS, null);
    }

    @Override
    public Report process(Map<String, Object> args) {
        Message succesMsg = new BaseFields.SuccessMessage("SUCCESS", "Successfully connected to LDAP");
        Message warningMsg = new BaseFields.WarningMessage("NO_ENCRYPTION", "The established connection was not upgraded to LDAPS. The connection is not secure.");
        Message failureMsg = new BaseFields.FailureMessage("CANNOT_CONNECT", "Failed to connect to the specified LDAP");
        return new BaseFields.BaseReport().messages(succesMsg, warningMsg, failureMsg);
    }
}
