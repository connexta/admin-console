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
package org.codice.ddf.admin.security.wcpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.karaf.jaas.config.JaasRealm;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.poller.BaseEnumValuePoller;

public class RealmTypesPoller extends BaseEnumValuePoller<JaasRealm, String> {

  private Map<String, String> descriptionMap = new HashMap<>();

  private List<JaasRealm> realms = new ArrayList<>();

  @Override
  public List<EnumValue<String>> getEnumValues() {
    return realms.stream().map(this::realmToEnumValue).collect(Collectors.toList());
  }

  public EnumValue<String> realmToEnumValue(JaasRealm realm) {
    return new EnumValue<String>() {
      @Override
      public String enumTitle() {
        return realm.getName();
      }

      @Override
      public String description() {
        return descriptionMap.get(realm.getName());
      }

      @Override
      public String value() {
        return realm.getName();
      }
    };
  }

  public void setDescriptionMap(Map<String, String> descriptionMap) {
    Map<String, String> newMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    newMap.putAll(descriptionMap);
    this.descriptionMap = newMap;
  }

  public void setRealms(List<JaasRealm> realms) {
    this.realms = realms;
  }
}
