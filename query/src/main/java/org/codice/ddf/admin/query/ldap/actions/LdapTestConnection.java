package org.codice.ddf.admin.query.ldap.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.Message;
import org.codice.ddf.admin.query.api.fields.Report;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapConnectionField;

public class LdapTestConnection  extends TestAction {
    public static final String ACTION_ID = "testConnect";
    public static final String ACTION_DESCRIPTION = "Attempts to established a connection with the given connection configuration";
    public static final List<Field> REQUIRED_FIELDS = Arrays.asList(new LdapConnectionField());

    public LdapTestConnection() {
        super(ACTION_ID, ACTION_DESCRIPTION, REQUIRED_FIELDS, null);
    }

    @Override
    public Report process(Map<String, Object> args) {
        Message succesMsg = new SuccessMessageField("SUCCESS", "Successfully connected to LDAP");
        Message warningMsg = new WarningMessageField("NO_ENCRYPTION", "The established connection was not upgraded to LDAPS. The connection is not secure.");
        Message failureMsg = new FailureMessageField("CANNOT_CONNECT", "Failed to connect to the specified LDAP");
        return new ReportField().messages(succesMsg, warningMsg, failureMsg);
    }
}
