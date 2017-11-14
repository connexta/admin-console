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
package org.codice.ddf.admin.query.dev.system.fields;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.Callable;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class PackageField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "pkg";

  public static final String FIELD_TYPE_NAME = "Package";

  public static final String DESCRIPTION =
      "A group of similar types of classes, interfaces and sub-packages.";

  public static final String BUNDLE_ID = "bundleId";

  public static final String NAME = "name";

  private StringField pkgName;

  private IntegerField bundleId;

  public PackageField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    pkgName = new StringField(NAME);
    bundleId = new IntegerField(BUNDLE_ID);
  }

  public PackageField pkgName(String name) {
    this.pkgName.setValue(name);
    return this;
  }

  public PackageField bundleId(int bundleId) {
    this.bundleId.setValue(bundleId);
    return this;
  }

  public String pkgName() {
    return pkgName.getValue();
  }

  public int bundleId() {
    return bundleId.getValue();
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(pkgName, bundleId);
  }

  public static class ListImpl extends BaseListField<PackageField> {

    public ListImpl(String fieldName) {
      super(fieldName);
    }

    @Override
    public Callable<PackageField> getCreateListEntryCallable() {
      return PackageField::new;
    }
  }
}
