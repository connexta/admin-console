package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapConnectionField;
import org.codice.ddf.admin.query.ldap.fields.LdapCredentialsField;

import com.google.common.collect.ImmutableList;

public class LdapTestBindField extends TestAction {

    public static final String NAME = "testBind";
    public static final String DESCRIPTION = "Attempts to bind a user to the given ldap connection given the ldap bind user credentials.";

    private LdapConnectionField connection;
    private LdapCredentialsField credentials;

    public LdapTestBindField() {
        super(NAME, DESCRIPTION);
        connection = new LdapConnectionField();
        credentials = new LdapCredentialsField();
    }

    @Override
    public ReportField process() {
        MessageField successMsg = new SuccessMessageField("SUCCESS", "Able to bind user to connection.");
        MessageField warningMsg = new WarningMessageField("WARNING", "Unable to bind user to connection.");
        MessageField failureMsg = new FailureMessageField("CANNOT_BIND", "Failed to connect to the specified LDAP");
        return new ReportField().messages(successMsg, warningMsg, failureMsg);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(connection, credentials);
    }
}
