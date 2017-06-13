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
package org.codice.ddf.admin.ldap.commons;

import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;

public class LdapMessages {

    public static final ErrorMessageImpl CANNOT_CONFIGURE = new ErrorMessageImpl("CANNOT_CONFIGURE");
    public static final ErrorMessageImpl CANNOT_CONNECT = new ErrorMessageImpl("CANNOT_CONNECT");
    public static final ErrorMessageImpl CANNOT_BIND = new ErrorMessageImpl("CANNOT_BIND");

    public static final ErrorMessageImpl BASE_USER_DN_NOT_FOUND = new ErrorMessageImpl("BASE_USER_DN_NOT_FOUND");
    public static final ErrorMessageImpl BASE_GROUP_DN_NOT_FOUND = new ErrorMessageImpl("BASE_GROUP_DN_NOT_FOUND");

    public static final ErrorMessageImpl NO_USERS_IN_BASE_USER_DN = new ErrorMessageImpl("NO_USERS_IN_BASE_USER_DN");
    public static final ErrorMessageImpl NO_GROUPS_IN_BASE_GROUP_DN = new ErrorMessageImpl("NO_GROUPS_IN_BASE_GROUP_DN");
    public static final ErrorMessageImpl NO_GROUPS_WITH_MEMBERS = new ErrorMessageImpl("NO_GROUPS_WITH_MEMBERS");
    public static final ErrorMessageImpl NO_REFERENCED_MEMBER = new ErrorMessageImpl("NO_REFERENCED_MEMBER");
    public static final ErrorMessageImpl USER_NAME_ATTRIBUTE_NOT_FOUND = new ErrorMessageImpl("USER_NAME_ATTRIBUTE_NOT_FOUND");

    public static ErrorMessageImpl invalidDnFormatError(String pathOrigin) {
        return  new ErrorMessageImpl("INVALID_DN", pathOrigin);
    }

    public static ErrorMessageImpl invalidQueryError(String pathOrigin) {
        return new ErrorMessageImpl("INVALID_QUERY", pathOrigin);
    }
}
