package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ReportField;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.BaseReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.BaseMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapSettingsField;

import com.google.common.collect.ImmutableList;

public class LdapTestSettings extends TestAction {
    public static final String ACTION_ID = "testSettings";
    public static final String DESCRIPTION = "Tests whether the given LDAP dn's and user attributes exist.";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapSettingsField());

    public LdapTestSettings() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null);
    }

    @Override
    public ReportField process(Map<String, Object> args) {
        BaseMessageField successMsg = new SuccessMessageField("SUCCESS", "All fields have been successfully validated.");
        BaseMessageField warningMsg = new WarningMessageField("WARNING", "No users in baseDN with the given attributes");
        BaseMessageField failureMsg = new FailureMessageField("CANNOT_BIND", "The specified user DN does not exist.");
        return new BaseReportField().messages(successMsg, warningMsg, failureMsg);
    }
}
