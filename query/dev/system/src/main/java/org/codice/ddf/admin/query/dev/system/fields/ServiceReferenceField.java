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

public class ServiceReferenceField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "ref";

  public static final String FIELD_TYPE_NAME = "ServiceReference";

  public static final String DESCRIPTION =
      "A reference to a particular service using the filter, and interface. Resolution states whether the service is required for the bundle that contains it to start up.";

  public static final String SERVICE_RESOLUTION = "resolution";

  public static final String SERVICE_FILTER = "filter";

  public static final String SERVICE_INTERFACE = "interface";

  public static final String MANDATORY = "mandatory";

  public static final String OPTIONAL = "optional";

  private StringField serviceInterface;
  private StringField filter;
  private StringField resolution;
  private ServiceField service;

  public ServiceReferenceField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    service = new ServiceField();
    resolution = new StringField(SERVICE_RESOLUTION);
    filter = new StringField(SERVICE_FILTER);
    serviceInterface = new StringField(SERVICE_INTERFACE);
  }

  public ServiceReferenceField service(ServiceField service) {
    this.service.setValue(service.getValue());
    return this;
  }

  public ServiceReferenceField filter(String filter) {
    this.filter.setValue(filter);
    return this;
  }

  public ServiceReferenceField serviceInterface(String interfacee) {
    this.serviceInterface.setValue(interfacee);
    return this;
  }

  public ServiceReferenceField resolution(long resolution) {
    this.resolution.setValue(getResolution(Math.toIntExact(resolution)));
    return this;
  }

  public ServiceReferenceField resolution(String resolution) {
    this.resolution.setValue(resolution);
    return this;
  }

  public String serviceInterface() {
    return serviceInterface.getValue();
  }

  public String filter() {
    return filter.getValue();
  }

  public String resolution() {
    return resolution.getValue();
  }

  public ServiceField service() {
    return service;
  }

  public static String getResolution(int i) {
    return i == 1 ? MANDATORY : OPTIONAL;
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(serviceInterface, filter, resolution, service);
  }

  public static class ListImpl extends BaseListField<ServiceReferenceField> {

    public static final String DEFAULT_FIELD_NAME = "refs";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    @Override
    public Callable<ServiceReferenceField> getCreateListEntryCallable() {
      return ServiceReferenceField::new;
    }
  }
}
