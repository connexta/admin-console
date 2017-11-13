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
import org.codice.ddf.admin.query.dev.system.fields.PackageField;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.GraphMLExporter;

public class PackageGraphProvider {

  private PackageGraphProvider() {}

  public static class PackageEdgeProvider
      implements ComponentAttributeProvider<DependencyEdge<PackageField>> {
    private static final String PKG_NAME = "package-name";

    private static final GraphMLAttribute PKG_NAME_ATTRI =
        new GraphMLAttribute(
            PKG_NAME, GraphMLExporter.AttributeCategory.EDGE, GraphMLExporter.AttributeType.STRING);

    public List<GraphMLAttribute> getAttributes() {
      return ImmutableList.of(PKG_NAME_ATTRI);
    }

    @Override
    public Map<String, String> getComponentAttributes(DependencyEdge<PackageField> pkg) {
      Map<String, String> attributes = new HashMap<>();
      attributes.put(PKG_NAME, pkg.value().pkgName());
      return attributes;
    }
  }
}
