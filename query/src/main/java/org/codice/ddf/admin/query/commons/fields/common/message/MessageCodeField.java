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
package org.codice.ddf.admin.query.commons.fields.common.message;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class MessageCodeField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "code";
    public static final String FIELD_TYPE_NAME = "MessageCode";
    public static final String DESCRIPTION = "An encapsulating description of what the message means.";

    public MessageCodeField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }
}
