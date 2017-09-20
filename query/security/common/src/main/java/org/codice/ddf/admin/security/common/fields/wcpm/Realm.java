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
package org.codice.ddf.admin.security.common.fields.wcpm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.api.poller.EnumValuePoller;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class Realm extends BaseEnumField<String> {

  public static final String DEFAULT_FIELD_NAME = "realm";

  public static final String FIELD_TYPE_NAME = "Realm";

  public static final String DESCRIPTION =
      "Authenticating Realms are used to authenticate an incoming authentication token and create a Subject on successful authentication.";

  public static final String REALM_POLLER_FILTER = "(dataTypeId=enum.values.realms)";

  private final ServiceReader serviceReader;

  public Realm(ServiceReader serviceReader) {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    this.serviceReader = serviceReader;
  }

  public Realm(ServiceReader serviceReader, EnumValue<String> value) {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    this.serviceReader = serviceReader;
    setValue(value.value());
  }

  @Override
  public List<EnumValue<String>> getEnumValues() {
    Set<EnumValuePoller> realms =
        serviceReader.getServices(EnumValuePoller.class, REALM_POLLER_FILTER);

    return realms
        .stream()
        .findFirst()
        .map(EnumValuePoller::getEnumValues)
        .orElse(new ArrayList<>());
  }

  @Override
  public Realm isRequired(boolean required) {
    super.isRequired(required);
    return this;
  }

  public static class ListImpl extends BaseListField<Realm> {

    public static final String DEFAULT_FIELD_NAME = "realms";

    private final ServiceReader serviceReader;

    public ListImpl(ServiceReader serviceReader) {
      super(DEFAULT_FIELD_NAME);
      this.serviceReader = serviceReader;
    }

    @Override
    public Callable<Realm> getCreateListEntryCallable() {
      return () -> new Realm(serviceReader);
    }

    @Override
    public ListImpl addAll(Collection<Realm> values) {
      super.addAll(values);
      return this;
    }
  }
}
