/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.common.fields.common;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;

import com.google.common.collect.ImmutableList;

public class HostField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "host";

    public static final String FIELD_TYPE_NAME = "Host";

    public static final String DESCRIPTION = "Represents a host identified by the hostname and the port. If this field is required, then"
            + " a host name and port must be provided.";

    private HostnameField hostname;

    private PortField port;

    public HostField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        hostname = new HostnameField();
        port = new PortField();
        updateInnerFieldPaths();
    }

    public HostField hostname(String hostname) {
        this.hostname.setValue(hostname);
        return this;
    }

    public HostField port(int port) {
        this.port.setValue(port);
        return this;
    }

    public Integer port() {
        return this.port.getValue();
    }

    public String hostname() {
        return this.hostname.getValue();
    }

    public HostnameField hostnameField() {
        return hostname;
    }

    public PortField portField() {
        return port;
    }

    public void useDefaultRequired() {
        hostname.isRequired(true);
        port.isRequired(true);
    }

    @Override
    public List<ErrorMessage> validate() {
        if(isRequired()) {
            hostname.isRequired(true);
            port.isRequired(true);
        }
        return super.validate();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(hostname, port);
    }
}
