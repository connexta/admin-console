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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.invalidHostnameError;

import java.util.List;
import java.util.regex.Pattern;

import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class HostnameField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "hostname";

    public static final String FIELD_TYPE_NAME = "Hostname";

    public static final String DESCRIPTION =
            "Must be between 1 and 63 characters long, and the entire hostname (including the delimiting dots but not a trailing dot)"
                    + " has a maximum of 253 ASCII characters.";

    private static final Pattern HOST_NAME_PATTERN = Pattern.compile("[0-9a-zA-Z.-]+");

    public HostnameField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public HostnameField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = super.validate();
        if(!validationMsgs.isEmpty()) {
            return validationMsgs;
        }

        if(getValue() != null && !validHostname(getValue())) {
            validationMsgs.add(invalidHostnameError(path()));
        }

        return validationMsgs;
    }

    public boolean validHostname(String hostname) {
        return HOST_NAME_PATTERN.matcher(hostname).matches();
    }
}