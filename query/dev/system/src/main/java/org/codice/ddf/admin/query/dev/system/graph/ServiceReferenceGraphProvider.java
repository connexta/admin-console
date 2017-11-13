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
import org.codice.ddf.admin.query.dev.system.fields.ServiceReferenceField;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.GraphMLExporter;

public class ServiceReferenceGraphProvider {

  private ServiceReferenceGraphProvider() {}

  public static class ServiceReferenceEdgeAttributeProvider
      implements ComponentAttributeProvider<DependencyEdge<ServiceReferenceField>> {

    public static final String SERVICE_NAME = "service-name";
    public static final String SERVICE_RESOLUTION = "service-resolution";
    public static final String SERVICE_FILTER = "service-filter";
    public static final String SERVICE_INTERFACE = "service-interface";

    private static final GraphMLAttribute SERVICE_NAME_ATTRI =
        new GraphMLAttribute(
            SERVICE_NAME,
            GraphMLExporter.AttributeCategory.EDGE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute SERVICE_RESOLUTION_ATTRI =
        new GraphMLAttribute(
            SERVICE_RESOLUTION,
            GraphMLExporter.AttributeCategory.EDGE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute SERVICE_FILTER_ATTRI =
        new GraphMLAttribute(
            SERVICE_FILTER,
            GraphMLExporter.AttributeCategory.EDGE,
            GraphMLExporter.AttributeType.STRING);

    private static final GraphMLAttribute SERVICE_INTERFACE_ATTRI =
        new GraphMLAttribute(
            SERVICE_INTERFACE,
            GraphMLExporter.AttributeCategory.EDGE,
            GraphMLExporter.AttributeType.STRING);

    @Override
    public Map<String, String> getComponentAttributes(DependencyEdge<ServiceReferenceField> ref) {
      Map<String, String> attributes = new HashMap<>();
      attributes.put(SERVICE_NAME, ref.value().service().serviceName());
      attributes.put(SERVICE_RESOLUTION, ref.value().resolution());
      attributes.put(SERVICE_FILTER, ref.value().filter() + "");
      attributes.put(SERVICE_INTERFACE, ref.value().serviceInterface());
      return attributes;
    }

    public List<GraphMLAttribute> getAttributes() {
      return ImmutableList.of(
          SERVICE_NAME_ATTRI,
          SERVICE_RESOLUTION_ATTRI,
          SERVICE_FILTER_ATTRI,
          SERVICE_INTERFACE_ATTRI);
    }
  }
}
