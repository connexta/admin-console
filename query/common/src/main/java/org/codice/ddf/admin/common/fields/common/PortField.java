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

import static org.codice.ddf.admin.common.message.DefaultMessages.invalidPortRangeError;

import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;

public class PortField extends IntegerField {

    public static final String DEFAULT_FIELD_NAME = "port";
    public static final String FIELD_TYPE_NAME  = "Port";
    public static final String DESCRIPTION = "Port range within the bounds of 0 - 65535";

    public PortField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public PortField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = super.validate();
        if(!validationMsgs.isEmpty()) {
            return validationMsgs;
        }

        if(getValue() != null && !validPortRange(getValue())) {
            validationMsgs.add(invalidPortRangeError(path()));
        }

        return validationMsgs;
    }

    public boolean validPortRange(int port) {
        return port > 0 && port < 65536;
    }
}
