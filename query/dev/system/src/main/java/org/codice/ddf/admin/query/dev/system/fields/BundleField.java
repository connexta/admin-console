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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.osgi.framework.Bundle;

import com.google.common.collect.ImmutableList;

public class BundleField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "bundle";

  public static final String FIELD_TYPE_NAME = "Bundle";

  public static final String DESCRIPTION =
      "A JAR containing OSGI information. For more information visit https://www.osgi.org/developer/specifications/ .";

  public static final String BUNDLE_ID = "id";

  public static final String BUNDLE_NAME = "name";

  public static final String BUNDLE_LOCATION = "location";

  public static final String BUNDLE_STATE = "state";

  public static final String EXPORTED_PKGS = "exportedPkgs";

  public static final String IMPORTED_PKGS = "importedPkgs";

  private IntegerField id;

  private StringField name;

  private StringField location;

  private StringField state;

  private PackageField.ListImpl exportedPkgs;

  private PackageField.ListImpl importedPkgs;

  private ServiceField.ListImpl services;

  private ServiceReferenceField.ListImpl refs;

  private ServiceReferenceListField.ListImpl refLists;

  public BundleField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    id = new IntegerField(BUNDLE_ID);
    name = new StringField(BUNDLE_NAME);
    location = new StringField(BUNDLE_LOCATION);
    state = new StringField(BUNDLE_STATE);
    services = new ServiceField.ListImpl();
    exportedPkgs = new PackageField.ListImpl(EXPORTED_PKGS);
    importedPkgs = new PackageField.ListImpl(IMPORTED_PKGS);
    refs = new ServiceReferenceField.ListImpl();
    refLists = new ServiceReferenceListField.ListImpl();
  }

  public BundleField id(int id) {
    this.id.setValue(id);
    return this;
  }

  public BundleField bundleName(String name) {
    this.name.setValue(name);
    return this;
  }

  public BundleField location(String location) {
    this.location.setValue(location);
    return this;
  }

  public BundleField addService(ServiceField ref) {
    services.add(ref);
    return this;
  }

  public BundleField addServiceRef(ServiceReferenceField ref) {
    refs.add(ref);
    return this;
  }

  public BundleField addServiceRefList(ServiceReferenceListField refList) {
    refLists.add(refList);
    return this;
  }

  public BundleField addExportedPackage(PackageField importedPkg) {
    exportedPkgs.add(importedPkg);
    return this;
  }

  public BundleField addImportedPackage(PackageField importedPkg) {
    importedPkgs.add(importedPkg);
    return this;
  }

  public Integer id() {
    return id.getValue();
  }

  public String bundleName() {
    return name.getValue();
  }

  public String location() {
    return location.getValue();
  }

  public String state() {
    return state.getValue();
  }

  public BundleField state(String state) {
    this.state.setValue(state);
    return this;
  }

  public BundleField state(int state) {
    this.state.setValue(getBundleState(state));
    return this;
  }

  public List<PackageField> exportedPackages() {
    return exportedPkgs.getList();
  }

  public List<PackageField> importedPackages() {
    return importedPkgs.getList();
  }

  public List<ServiceReferenceListField> serviceRefLists() {
    return refLists.getList();
  }

  public List<ServiceReferenceField> serviceRefs() {
    return refs.getList();
  }

  public List<ServiceField> services() {
    return services.getList();
  }

  public static String getBundleState(int state) {
    switch (state) {
      case Bundle.UNINSTALLED:
        return "UNINSTALLED";
      case Bundle.INSTALLED:
        return "INSTALLED";
      case Bundle.RESOLVED:
        return "RESOLVED";
      case Bundle.STARTING:
        return "STARTING";
      case Bundle.STOPPING:
        return "STOPPING";
      case Bundle.ACTIVE:
        return "ACTIVE";
      default:
        return "UNDEFINED";
    }
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(
        id, name, location, state, services, exportedPkgs, importedPkgs, refs, refLists);
  }

  public static class ListImpl extends BaseListField<BundleField> {

    public static final String DEFAULT_FIELD_NAME = "bundles";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    public ListImpl(String fieldName) {
      super(fieldName);
    }

    @Override
    public Callable<BundleField> getCreateListEntryCallable() {
      return BundleField::new;
    }

    @Override
    public ListImpl addAll(Collection<BundleField> values) {
      super.addAll(values);
      return this;
    }
  }
}
