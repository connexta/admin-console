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

public class ServiceField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "service";

  public static final String FIELD_TYPE_NAME = "Service";

  public static final String DESCRIPTION =
      "An OSGI java object instance, registered into an OSGi framework with a set of properties.";

  public static final String BUNDLE_ID = "bundleId";

  public static final String SERVICE_NAME = "name";

  private StringField bundleName;

  private IntegerField bundleId;

  public ServiceField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    bundleName = new StringField(SERVICE_NAME);
    bundleId = new IntegerField(BUNDLE_ID);
  }

  public ServiceField serviceName(String name) {
    this.bundleName.setValue(name);
    return this;
  }

  public ServiceField bundleId(long bundleId) {
    this.bundleId.setValue(Math.toIntExact(bundleId));
    return this;
  }

  public String serviceName() {
    return bundleName.getValue();
  }

  public int bundleId() {
    return bundleId.getValue();
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(bundleName, bundleId);
  }

  public static class ListImpl extends BaseListField<ServiceField> {

    public static final String DEFAULT_FIELD_NAME = "services";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    @Override
    public Callable<ServiceField> getCreateListEntryCallable() {
      return ServiceField::new;
    }
  }
}
