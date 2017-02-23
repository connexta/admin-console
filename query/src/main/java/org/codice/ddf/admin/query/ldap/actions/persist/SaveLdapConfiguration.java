package org.codice.ddf.admin.query.ldap.actions.persist;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.DefaultAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveLdapConfiguration extends DefaultAction<ReportField> {

    public static final String ACTION_ID = "save";
    public static final String DESCRIPTION = "Saves the LDAP configuration.";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapConfigurationField());

    public SaveLdapConfiguration() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null, new ReportField());
    }

    @Override
    public ReportField process(Map<String, Object> args) {
        return new ReportField().messages(
                new SuccessMessageField("SUCCESS", "Successfully saved the configuration."),
                new SuccessMessageField("SUCCESS", "Successfully updated the configuration."),
                new FailureMessageField("FAILED", "Unable to save configuration."));
    }
}
