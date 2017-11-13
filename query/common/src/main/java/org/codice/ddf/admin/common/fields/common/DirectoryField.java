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
package org.codice.ddf.admin.common.fields.common;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.DIRECTORY_DOES_NOT_EXIST;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.directoryDoesNotExist;

import com.google.common.collect.ImmutableSet;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class DirectoryField extends StringField {

  public static final String DEFAULT_FIELD_NAME = "dir";

  public static final String FIELD_TYPE_NAME = "Directory";

  public static final String DESCRIPTION = "Specifies a unique directory in a file system.";

  private boolean validateDirExists = false;

  public DirectoryField() {
    this(DEFAULT_FIELD_NAME);
  }

  public DirectoryField(String fieldName) {
    super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
  }

  public DirectoryField validateDirectoryExists() {
    validateDirExists = true;
    return this;
  }

  @Override
  public List<ErrorMessage> validate() {
    List<ErrorMessage> errors = super.validate();
    if (!errors.isEmpty()) {
      return errors;
    }

    if (getValue() != null && validateDirExists && !Paths.get(getValue()).toFile().exists()) {
      errors.add(directoryDoesNotExist(getPath()));
    }

    return errors;
  }

  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .add(DIRECTORY_DOES_NOT_EXIST)
        .build();
  }
}
