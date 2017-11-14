/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.ldap.commons;

import java.util.List;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;

public class LdapMessages {
  public static final String MD5_NEEDS_ENCRYPTED = "MD5_NEEDS_ENCRYPTED";

  public static final String CANNOT_BIND = "CANNOT_BIND";

  public static final String INVALID_DN = "INVALID_DN";

  public static final String INVALID_QUERY = "INVALID_QUERY";

  public static final String DN_DOES_NOT_EXIST = "DN_DOES_NOT_EXIST";

  public static final String NO_USERS_IN_BASE_USER_DN = "NO_USERS_IN_BASE_USER_DN";

  public static final String NO_GROUPS_IN_BASE_GROUP_DN = "NO_GROUPS_IN_BASE_GROUP_DN";

  public static final String NO_GROUPS_WITH_MEMBERS = "NO_GROUPS_WITH_MEMBERS";

  public static final String NO_REFERENCED_MEMBER = "NO_REFERENCED_MEMBER";

  public static final String USER_ATTRIBUTE_NOT_FOUND = "USER_ATTRIBUTE_NOT_FOUND";

  public static final String INVALID_USER_ATTRIBUTE = "INVALID_USER_ATTRIBUTE";

  private LdapMessages() {}

  public static ErrorMessage invalidDnFormatError(List<Object> path) {
    return new ErrorMessageImpl(INVALID_DN, path);
  }

  public static ErrorMessage invalidQueryError(List<Object> path) {
    return new ErrorMessageImpl(INVALID_QUERY, path);
  }

  public static ErrorMessage dnDoesNotExistError(List<Object> pathOrigin) {
    return new ErrorMessageImpl(DN_DOES_NOT_EXIST, pathOrigin);
  }

  public static ErrorMessage noUsersInBaseUserDnError(List<Object> path) {
    return new ErrorMessageImpl(NO_USERS_IN_BASE_USER_DN, path);
  }

  public static ErrorMessage noGroupsInBaseGroupDnError(List<Object> path) {
    return new ErrorMessageImpl(NO_GROUPS_IN_BASE_GROUP_DN, path);
  }

  public static ErrorMessage noGroupsWithMembersError(List<Object> path) {
    return new ErrorMessageImpl(NO_GROUPS_WITH_MEMBERS, path);
  }

  public static ErrorMessage noReferencedMemberError(List<Object> path) {
    return new ErrorMessageImpl(NO_REFERENCED_MEMBER, path);
  }

  public static ErrorMessage userAttributeNotFoundError(List<Object> path) {
    return new ErrorMessageImpl(USER_ATTRIBUTE_NOT_FOUND, path);
  }

  public static ErrorMessage md5NeedsEncryptedError(List<Object> path) {
    return new ErrorMessageImpl(MD5_NEEDS_ENCRYPTED, path);
  }

  public static ErrorMessage invalidUserAttribute(List<Object> path) {
    return new ErrorMessageImpl(INVALID_USER_ATTRIBUTE, path);
  }

  static ErrorMessage cannotBindError(List<Object> path) {
    return new ErrorMessageImpl(CANNOT_BIND, path);
  }
}
