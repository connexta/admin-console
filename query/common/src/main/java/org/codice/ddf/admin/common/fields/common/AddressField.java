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
 */
package org.codice.ddf.admin.common.fields.common;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;

import com.google.common.collect.ImmutableList;

public class AddressField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "address";

    public static final String FIELD_TYPE_NAME = "Address";

    public static final String DESCRIPTION =
            "Represents an address given by either a hostname and port or a URL. When this field is required,"
                    + " a URL or hostname and port must be provided.";

    private HostField host;

    private UrlField url;

    public AddressField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        host = new HostField();
        url = new UrlField();
        updateInnerFieldPaths();
    }

    public AddressField hostname(String hostname) {
        host.hostname(hostname);
        return this;
    }

    public AddressField port(int port) {
        host.port(port);
        return this;
    }

    public AddressField url(String url) {
        this.url.setValue(url);
        return this;
    }

    public HostField host() {
        return host;
    }

    public String url() {
        return url.getValue();
    }

    public UrlField urlField() {
        return url;
    }

    @Override
    public List<Message> validate() {
        if(isRequired() && url.getValue() == null && (host.hostnameField().getValue() != null || host.portField().getValue() != null)) {
            host.isRequired(true);
        } else if(isRequired()) {
            url.isRequired(true);
        }
        return super.validate();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(host, url);
    }
}
