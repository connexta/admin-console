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
import org.codice.ddf.admin.query.ldap.fields.LdapSettingsField;

import com.google.common.collect.ImmutableList;

public class LdapTestSettingsField extends TestActionField {
    public static final String FIELD_NAME = "testSettings";
    public static final String DESCRIPTION = "Tests whether the given LDAP dn's and user attributes exist.";

    private LdapSettingsField settings = new LdapSettingsField();
    private List<Field> arguments = ImmutableList.of(settings);

    public LdapTestSettingsField() {
        super(FIELD_NAME, DESCRIPTION);
    }

    @Override
    public ReportField process(Map<String, Object> args) {
        BaseMessageField successMsg = new SuccessMessageField("SUCCESS", "All fields have been successfully validated.");
        BaseMessageField warningMsg = new WarningMessageField("WARNING", "No users in baseDN with the given attributes");
        BaseMessageField failureMsg = new FailureMessageField("CANNOT_BIND", "The specified user DN does not exist.");
        return new BaseReportField().messages(successMsg, warningMsg, failureMsg);
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
