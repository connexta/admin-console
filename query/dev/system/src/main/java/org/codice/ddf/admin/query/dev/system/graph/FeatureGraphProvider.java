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
package org.codice.ddf.admin.query.dev.system.graph;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.query.dev.system.fields.BundleField;
import org.codice.ddf.admin.query.dev.system.fields.FeatureField;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.GraphMLExporter;

public class FeatureGraphProvider {

  private FeatureGraphProvider() {}

  public static class FeatureOrBundleVertexAttributeProvider
      implements ComponentAttributeProvider<Field> {

    private FeatureVertexAttributeProvider featureVertexProv;
    private BundleGraphProvider.BundleVertexAttributeProvider bundleVertexProv;

    public FeatureOrBundleVertexAttributeProvider() {
      featureVertexProv = new FeatureVertexAttributeProvider();
      bundleVertexProv = new BundleGraphProvider.BundleVertexAttributeProvider();
    }

    @Override
    public Map<String, String> getComponentAttributes(Field component) {
      if (component instanceof FeatureField) {
        return featureVertexProv.getComponentAttributes((FeatureField) component);
      } else {
        return bundleVertexProv.getComponentAttributes((BundleField) component);
      }
    }

    public List<GraphMLAttribute> getAttributes() {
      return new ImmutableList.Builder<GraphMLAttribute>()
          .addAll(featureVertexProv.getAttributes())
          .addAll(bundleVertexProv.getAttributes())
          .build();
    }
  }

  public static class FeatureVertexAttributeProvider
      implements ComponentAttributeProvider<FeatureField> {

    private static final String FEATURE_NAME = "feature-name";

    private static final String FEATURE_ID = "feature-id";

    private static final String FEATURE_STATE = "feature-install-type";

    private static final String FEATURE_DESCRIPTION = "feature-description";

    private static final String FEATURE_REPO_URL = "feature-repo-url";

    private static final GraphMLAttribute FEATURE_NAME_ATTRI =
        new GraphMLAttribute(
            FEATURE_NAME,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute FEATURE_ID_ATTRI =
        new GraphMLAttribute(
            FEATURE_ID,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute FEATURE_STATE_ATTRI =
        new GraphMLAttribute(
            FEATURE_STATE,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute FEATURE_DESCRIPTION_ATTRI =
        new GraphMLAttribute(
            FEATURE_DESCRIPTION,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute FEATURE_REPO_URL_ATTRI =
        new GraphMLAttribute(
            FEATURE_REPO_URL,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    @Override
    public Map<String, String> getComponentAttributes(FeatureField feature) {
      Map<String, String> attributes = new HashMap<>();
      attributes.put(FEATURE_NAME, feature.name());
      attributes.put(FEATURE_ID, feature.id());
      attributes.put(FEATURE_STATE, feature.state());
      attributes.put(FEATURE_DESCRIPTION, feature.featDescription());
      attributes.put(FEATURE_REPO_URL, feature.repoUrl());
      return attributes;
    }

    public List<GraphMLAttribute> getAttributes() {
      return ImmutableList.of(
          FEATURE_NAME_ATTRI,
          FEATURE_ID_ATTRI,
          FEATURE_STATE_ATTRI,
          FEATURE_DESCRIPTION_ATTRI,
          FEATURE_REPO_URL_ATTRI);
    }
  }
}
