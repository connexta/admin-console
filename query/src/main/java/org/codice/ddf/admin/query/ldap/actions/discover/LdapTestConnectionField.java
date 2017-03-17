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

import com.google.common.collect.ImmutableList;

public class LdapTestConnectionField extends TestAction {

    public static final String NAME = "testConnect";
    public static final String DESCRIPTION = "Attempts to established a connection with the given connection configuration";

    private LdapConnectionField connection;

    public LdapTestConnectionField() {
        super(NAME, DESCRIPTION);
        connection = new LdapConnectionField();
    }

    @Override
    public ReportField process() {
        MessageField succesMsg = new SuccessMessageField("SUCCESS", "Successfully connected to LDAP");
        MessageField warningMsg = new WarningMessageField("NO_ENCRYPTION", "The established connection was not upgraded to LDAPS. The connection is not secure.");
        MessageField failureMsg = new FailureMessageField("CANNOT_CONNECT", "Failed to connect to the specified LDAP");
        return new ReportField().messages(succesMsg, warningMsg, failureMsg);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(connection);
    }
}
