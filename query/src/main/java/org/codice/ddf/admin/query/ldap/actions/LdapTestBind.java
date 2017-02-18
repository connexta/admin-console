package org.codice.ddf.admin.query.ldap.actions;

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
import org.codice.ddf.admin.query.ldap.fields.LdapCredentialsField;

import com.google.common.collect.ImmutableList;

public class LdapTestBind extends TestAction {

    public static final String ACTION_ID = "testBind";
    public static final String DESCRIPTION = "Attempts to bind a user to the given ldap connection given the ldap bind user credentials.";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapConnectionField(), new LdapCredentialsField());

    public LdapTestBind() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null);
    }

    @Override
    public Report process(Map<String, Object> args) {
        Message successMsg = new SuccessMessageField("SUCCESS", "Able to bind user to connection.");
        Message warningMsg = new WarningMessageField("WARNING", "Unable to bind user to connection.");
        Message failureMsg = new FailureMessageField("CANNOT_BIND", "Failed to connect to the specified LDAP");
        return new ReportField().messages(successMsg, warningMsg, failureMsg);
    }
}
