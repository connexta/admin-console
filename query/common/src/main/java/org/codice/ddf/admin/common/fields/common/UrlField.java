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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.invalidUrlError;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class UrlField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "url";

    public static final String FIELD_TYPE_NAME = "URL";

    public static final String DESCRIPTION =
            "An address that identifies a particular file on the Internet, usually consisting of the protocol, such as HTTP, followed by the domain name.";

    public UrlField() {
        this(DEFAULT_FIELD_NAME);
    }

    public UrlField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public UrlField url(String url) {
        setValue(url);
        return this;
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = super.validate();
        if (!validationMsgs.isEmpty()) {
            return validationMsgs;
        }

        if (getValue() != null) {
            try {
                new URI(getValue().trim()).toURL();
            } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
                validationMsgs.add(invalidUrlError(path()));
            }
        }
        return validationMsgs;
    }
}
