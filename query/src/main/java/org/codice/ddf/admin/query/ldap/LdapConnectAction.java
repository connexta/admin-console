package org.codice.ddf.admin.query.ldap;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.Field;
import org.codice.ddf.admin.query.commons.action.DefaultAction;
import org.codice.ddf.admin.query.commons.DefaultActionReport;
import org.codice.ddf.admin.query.commons.DefaultFields;

import com.google.common.collect.ImmutableList;

public class LdapConnectAction extends DefaultAction{

    public static final String SOME_RETURN_TYPE = "someReturnField";
    public static final String SOME_RETURN_TYPE_2 = "someReturnField2";

    public static final String LDAP_HOST_NAME = "ldapHostname";
    public static final String LDAP_PORT = "ldapPort";

    private static final List<Field> RETURN_TYPES = ImmutableList.of(new DefaultFields.StringField(SOME_RETURN_TYPE
            ), new DefaultFields.IntegerField(SOME_RETURN_TYPE_2));

    private static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new DefaultFields.StringField(LDAP_HOST_NAME
    ), new DefaultFields.IntegerField(LDAP_PORT));

    public LdapConnectAction() {
        super("connect", "Connect to a given LDAP. ", REQUIRED_FIELDS, null, RETURN_TYPES);
    }

    @Override
    public ActionReport process(Map<String, Object> args) {
        DefaultActionReport report = new DefaultActionReport();
        report.addValue(SOME_RETURN_TYPE, "Sample field addValue");
        report.addValue(SOME_RETURN_TYPE_2, 666);
        return report;
    }

    @Override
    public ActionReport validate(List<Field> args) {
        return new DefaultActionReport();
    }

    @Override
    public List<Field> getReturnTypes() {
        return RETURN_TYPES;
    }
}
