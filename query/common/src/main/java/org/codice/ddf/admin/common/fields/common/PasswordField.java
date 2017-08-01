
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

import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class PasswordField extends StringField{

    public static final String DEFAULT_FIELD_NAME = "password";

    public static final String FIELD_TYPE_NAME = "Password";

    public static final String DESCRIPTION = "Password used for authentication";

    // A flag to indicate if a service being updated has a password of "secret".
    public static final String FLAG_PASSWORD = "secret";

    // Determine which calls need the real password and which ones get the flag password
    private boolean internalProcess;

    public PasswordField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        internalProcess = false;
    }

    public PasswordField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
        internalProcess = false;
    }

    /**
     * Returns the flag password if the calling process is graphQL. This is used to exclude the password when logging.
     *
     * @return {@code null}, {@code FLAG_PASSWORD}, or {@code value}
     **/
    @Override
    public String getValue() {
        if(super.getValue() == null){
            return null;
        }
        if(!internalProcess) {
            return FLAG_PASSWORD;
        }
        return getRealPassword();
    }

    /**
     * Returns the real password that was stored. This method is only used internally.
     *
     * @return {@code value}
     */
    public String getRealPassword(){
        return super.getValue();
    }

    /**
     * Identifies internal calls versus graphQL calls.
     */
    public void markAsInternalProcess(){
        internalProcess = true;
    }

    public boolean isInternalProcess() {
        return internalProcess;
    }
}
