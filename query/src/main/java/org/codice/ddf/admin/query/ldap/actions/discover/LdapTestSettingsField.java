package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapSettingsField;

import com.google.common.collect.ImmutableList;

public class LdapTestSettingsField extends TestAction {
    public static final String NAME = "testSettings";
    public static final String DESCRIPTION = "Tests whether the given LDAP dn's and user attributes exist.";

    private LdapSettingsField settings;

    public LdapTestSettingsField() {
        super(NAME, DESCRIPTION);
        settings = new LdapSettingsField();
    }

    @Override
    public ReportField process() {
        MessageField successMsg = new SuccessMessageField("SUCCESS", "All fields have been successfully validated.");
        MessageField warningMsg = new WarningMessageField("WARNING", "No users in baseDN with the given attributes");
        MessageField failureMsg = new FailureMessageField("CANNOT_BIND", "The specified user DN does not exist.");
        return new ReportField().messages(successMsg, warningMsg, failureMsg);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(settings);
    }
}
