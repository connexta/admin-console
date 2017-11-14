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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.UrlField;

public class FeatureField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "feature";
  public static final String FIELD_TYPE_NAME = "Feature";
  public static final String DESCRIPTION =
      "A Karaf feature describes an application as a name, version, description, set of bundles, configurations and dependency features.";

  public static final String FEATURE_NAME = "name";
  public static final String FEATURE_STATE = "state";
  public static final String FEATURE_DESCRIPTION = "description";
  public static final String FEATURE_ID = "id";
  public static final String FEATURE_REPO_URL = "repoUrl";
  public static final String FEATURE_DEPS = "featureDeps";
  public static final String BUNDLE_DEPS = "bundleDeps";

  private StringField id;
  private StringField name;
  private StringField state;
  private StringField featDescription;
  private UrlField repoUrl;
  private StringField.ListImpl featDeps;
  private BundleField.ListImpl bundleDeps;

  public FeatureField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    id = new StringField(FEATURE_ID);
    name = new StringField(FEATURE_NAME);
    state = new StringField(FEATURE_STATE);
    featDescription = new StringField(FEATURE_DESCRIPTION);
    repoUrl = new UrlField(FEATURE_REPO_URL);
    featDeps = new StringField.ListImpl(FEATURE_DEPS);
    bundleDeps = new BundleField.ListImpl(BUNDLE_DEPS);
  }

  public FeatureField name(String name) {
    this.name.setValue(name);
    return this;
  }

  public FeatureField state(String state) {
    this.state.setValue(state);
    return this;
  }

  public FeatureField featDescription(String description) {
    this.featDescription.setValue(description);
    return this;
  }

  public FeatureField id(String id) {
    this.id.setValue(id);
    return this;
  }

  public FeatureField repoUrl(String uri) {
    this.repoUrl.setValue(uri);
    return this;
  }

  public FeatureField addFeatureDeps(List<String> featsToAdd) {
    for (String featId : featsToAdd) {
      StringField newStr = new StringField();
      newStr.setValue(featId);
      featDeps.add(newStr);
    }

    return this;
  }

  public FeatureField addBundleDeps(List<BundleField> bundles) {
    bundleDeps.addAll(bundles);
    return this;
  }

  public String id() {
    return id.getValue();
  }

  public String name() {
    return name.getValue();
  }

  public String state() {
    return state.getValue();
  }

  public String featDescription() {
    return featDescription.getValue();
  }

  public String repoUrl() {
    return repoUrl.getValue();
  }

  public List<String> featDeps() {
    return featDeps.getValue();
  }

  public List<BundleField> bundleDeps() {
    return bundleDeps.getList();
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(id, name, state, featDescription, repoUrl, featDeps, bundleDeps);
  }

  public static class ListImpl extends BaseListField<FeatureField> {

    public static final String DEFAULT_FIELD_NAME = "features";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    public ListImpl(String fieldName) {
      super(fieldName);
    }

    @Override
    public Callable<FeatureField> getCreateListEntryCallable() {
      return FeatureField::new;
    }

    @Override
    public ListImpl addAll(Collection<FeatureField> values) {
      super.addAll(values);
      return this;
    }
  }
}
