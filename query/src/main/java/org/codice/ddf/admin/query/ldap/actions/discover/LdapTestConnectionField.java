package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ReportField;
import org.codice.ddf.admin.query.commons.actions.TestActionField;
import org.codice.ddf.admin.query.commons.fields.common.BaseReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.BaseMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapConnectionField;

import com.google.common.collect.ImmutableList;

public class LdapTestConnectionField extends TestActionField {
    public static final String FIELD_NAME = "testConnect";
    public static final String DESCRIPTION = "Attempts to established a connection with the given connection configuration";

    private LdapConnectionField connection = new LdapConnectionField();
    private List<Field> arguments = ImmutableList.of(connection);

    public LdapTestConnectionField() {
        super(FIELD_NAME, DESCRIPTION);
    }

    @Override
    public ReportField process(Map<String, Object> args) {
        BaseMessageField succesMsg = new SuccessMessageField("SUCCESS", "Successfully connected to LDAP");
        BaseMessageField warningMsg = new WarningMessageField("NO_ENCRYPTION", "The established connection was not upgraded to LDAPS. The connection is not secure.");
        BaseMessageField failureMsg = new FailureMessageField("CANNOT_CONNECT", "Failed to connect to the specified LDAP");
        return new BaseReportField().messages(succesMsg, warningMsg, failureMsg);
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
