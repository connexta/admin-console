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
package org.codice.ddf.admin.ldap.actions.commons;

import org.codice.ddf.admin.common.message.ErrorMessage;
import org.codice.ddf.admin.common.message.WarningMessage;

public class LdapMessages {

    public static final ErrorMessage CANNOT_CONFIGURE = new ErrorMessage("CANNOT_CONFIGURE");
    public static final ErrorMessage CANNOT_CONNECT = new ErrorMessage("CANNOT_CONNECT");
    public static final ErrorMessage CANNOT_BIND = new ErrorMessage("CANNOT_BIND");

    public static final ErrorMessage BASE_USER_DN_NOT_FOUND = new ErrorMessage("BASE_USER_DN_NOT_FOUND");
    public static final ErrorMessage BASE_GROUP_DN_NOT_FOUND = new ErrorMessage("BASE_GROUP_DN_NOT_FOUND");

    public static final WarningMessage NO_USERS_IN_BASE_USER_DN = new WarningMessage("NO_USERS_IN_BASE_USER_DN");
    public static final WarningMessage NO_GROUPS_IN_BASE_GROUP_DN = new WarningMessage("NO_GROUPS_IN_BASE_GROUP_DN");
    public static final WarningMessage NO_GROUPS_WITH_MEMBERS = new WarningMessage("NO_GROUPS_WITH_MEMBERS");
    public static final WarningMessage NO_REFERENCED_MEMBER = new WarningMessage("NO_REFERENCED_MEMBER");
    public static final WarningMessage USER_NAME_ATTRIBUTE_NOT_FOUND = new WarningMessage("USER_NAME_ATTRIBUTE_NOT_FOUND");

    public static ErrorMessage invalidDnFormatError(String pathOrigin) {
        return  new ErrorMessage("INVALID_DN", pathOrigin);
    }

    public static ErrorMessage invalidQueryError(String pathOrigin) {
        return new ErrorMessage("INVALID_QUERY", pathOrigin);
    }
}
