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
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class ServiceReferenceListField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "refList";

  public static final String FIELD_TYPE_NAME = "ServiceReferenceList";

  public static final String SERVICE_FILTER = "filter";

  public static final String SERVICE_RESOLUTION = "resolution";

  public static final String SERVICE_INTERFACE = "interface";

  public static final String DESCRIPTION =
      "A list of services all matching the specified filter and interface. Resolution states whether at least one service matching the criteria is required for the bundle that contains it to start up.";

  public static final String MANDATORY = "mandatory";

  public static final String OPTIONAL = "optional";

  private ServiceField.ListImpl services;
  private StringField filter;
  private StringField resolution;
  private StringField serviceInterface;

  public ServiceReferenceListField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    services = new ServiceField.ListImpl();
    filter = new StringField(SERVICE_FILTER);
    resolution = new StringField(SERVICE_RESOLUTION);
    serviceInterface = new StringField(SERVICE_INTERFACE);
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(services, filter, resolution, serviceInterface);
  }

  public ServiceReferenceListField filter(String filter) {
    this.filter.setValue(filter);
    return this;
  }

  public ServiceReferenceListField referenceListInterface(String refListInterface) {
    this.serviceInterface.setValue(refListInterface);
    return this;
  }

  public ServiceReferenceListField addService(ServiceField serviceField) {
    services.add(serviceField);
    return this;
  }

  public ServiceReferenceListField resolution(long resolution) {
    this.resolution.setValue(getResolution(Math.toIntExact(resolution)));
    return this;
  }

  public List<ServiceField> services() {
    return services.getList();
  }

  public String filter() {
    return filter.getValue();
  }

  public String resolution() {
    return resolution.getValue();
  }

  public String referenceListInterface() {
    return serviceInterface.getValue();
  }

  private static String getResolution(int i) {
    return i == 1 ? MANDATORY : OPTIONAL;
  }

  public static class ListImpl extends BaseListField<ServiceReferenceListField> {

    public static final String DEFAULT_FIELD_NAME = "refLists";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    @Override
    public Callable<ServiceReferenceListField> getCreateListEntryCallable() {
      return ServiceReferenceListField::new;
    }
  }
}
