package org.codice.ddf.admin.query.ldap.actions.persist;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.commons.fields.common.BaseReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveLdapConfiguration extends BaseActionField<BaseReportField> {

    public static final String FIELD_NAME = "save";
    public static final String DESCRIPTION = "Saves the LDAP configuration.";
    private LdapConfigurationField config = new LdapConfigurationField();
    private List<Field> arguments = ImmutableList.of(config);

    public SaveLdapConfiguration() {
        super(FIELD_NAME, DESCRIPTION, new BaseReportField());
    }

    @Override
    public BaseReportField process(Map<String, Object> args) {
        return new BaseReportField().messages(
                new SuccessMessageField("SUCCESS", "Successfully saved the configuration."),
                new SuccessMessageField("SUCCESS", "Successfully updated the configuration."),
                new FailureMessageField("FAILED", "Unable to save configuration."));
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
