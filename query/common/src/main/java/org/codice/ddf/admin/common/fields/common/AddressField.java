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

import static org.codice.ddf.admin.common.message.DefaultMessages.missingRequiredFieldError;

import java.util.Collections;
import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;

import com.google.common.collect.ImmutableList;

public class AddressField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "address";

    public static final String FIELD_TYPE_NAME = "Address";

    public static final String DESCRIPTION = "Represents an address given by either a hostname and port or a URL. If no fields are provided"
            + " then a missing required field error will be given on the URL. If the URL is not provided and only one of the port or hostname are provided"
            + " then a missing required field error will be returned for the appropriate missing field.";

    private HostField host;

    private UrlField url;

    public AddressField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public AddressField hostname(String hostname) {
        host.name(hostname);
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
        if (url.getValue() == null) {
            if (host.port() != null && host.name() != null) {
                return host.validate();
            }
            if (host.port() == null && host.name() != null) {
                return Collections.singletonList(missingRequiredFieldError(host.portField().path()));
            }
            if (host.name() == null && host.port() != null) {
                return Collections.singletonList(missingRequiredFieldError(host.hostnameField().path()));
            }
            return Collections.singletonList(missingRequiredFieldError(url.path()));
        }
        return url.validate();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(host, url);
    }

    @Override
    public void initializeFields() {
        host = new HostField();
        url = new UrlField();
    }
}
