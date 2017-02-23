package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.EnumFieldValue;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.common.HostnameField;
import org.codice.ddf.admin.query.commons.fields.common.PortField;

import com.google.common.collect.ImmutableList;

public class LdapConnectionField extends ObjectField {
    public static final String FIELD_NAME = "connection";
    public static final String FIELD_TYPE_NAME = "LdapConnection";
    public static final String DESCRIPTION = "Contains the required information to establish an LDAP connection.";
    private HostnameField hostname;
    private PortField port;
    private LdapEncryptionMethodField encryptionMethod;

    public LdapConnectionField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        hostname = new HostnameField();
        port = new PortField();
        encryptionMethod = new LdapEncryptionMethodField();
    }

    public LdapConnectionField hostname(String hostname) {
        this.hostname.setValue(hostname);
        return this;
    }

    public LdapConnectionField port(int port){
        this.port.setValue(port);
        return this;
    }

    public LdapConnectionField encryptionMethod(LdapEncryptionMethodField encryptionMethod) {
        this.encryptionMethod.setValue(encryptionMethod.getValue());
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(hostname, port, encryptionMethod);
    }
}
