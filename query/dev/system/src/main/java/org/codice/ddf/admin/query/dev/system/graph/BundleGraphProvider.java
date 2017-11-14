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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.dev.system.fields.BundleField;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.GraphMLExporter;

import com.google.common.collect.ImmutableList;

public class BundleGraphProvider {

  private BundleGraphProvider() {}

  public static class BundleVertexAttributeProvider
      implements ComponentAttributeProvider<BundleField> {

    public static final String BUNDLE_NAME = "bundle-name";
    public static final String BUNDLE_ID = "bundle-id";
    public static final String BUNDLE_LOCATION = "bundle-location";
    public static final String BUNDLE_STATE = "bundle-state";

    private static final GraphMLAttribute BUNDLE_NAME_ATTRI =
        new GraphMLAttribute(
            BUNDLE_NAME,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute BUNDLE_ID_ATTRI =
        new GraphMLAttribute(
            BUNDLE_ID, GraphMLExporter.AttributeCategory.NODE, GraphMLExporter.AttributeType.INT);

    private static final GraphMLAttribute BUNDLE_LOCATION_ATTRI =
        new GraphMLAttribute(
            BUNDLE_LOCATION,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute BUNDLE_STATE_ATTRI =
        new GraphMLAttribute(
            BUNDLE_STATE,
            GraphMLExporter.AttributeCategory.NODE,
            GraphMLExporter.AttributeType.STRING);

    public List<GraphMLAttribute> getAttributes() {
      return ImmutableList.of(
          BUNDLE_NAME_ATTRI, BUNDLE_ID_ATTRI, BUNDLE_LOCATION_ATTRI, BUNDLE_STATE_ATTRI);
    }

    @Override
    public Map<String, String> getComponentAttributes(BundleField bundle) {
      Map<String, String> attributes = new HashMap<>();
      attributes.put(BUNDLE_NAME_ATTRI.getAttriName(), bundle.bundleName());
      attributes.put(BUNDLE_ID_ATTRI.getAttriName(), bundle.id() + "");
      attributes.put(BUNDLE_LOCATION_ATTRI.getAttriName(), bundle.location());
      attributes.put(BUNDLE_STATE_ATTRI.getAttriName(), bundle.state());
      return attributes;
    }
  }
}
