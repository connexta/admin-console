package org.codice.ddf.admin.query.commons.fields.common;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;

import com.google.common.collect.ImmutableList;

public class AddressField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "address";
    public static final String FIELD_TYPE_NAME  = "Address";
    public static final String DESCRIPTION = "Represents a url base address.";

    private HostnameField hostname;
    private PortField port;

    public AddressField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.hostname = new HostnameField();
        this.port = new PortField();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(hostname, port);
    }
}
