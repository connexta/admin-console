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

import org.jgrapht.ext.GraphMLExporter;

public class GraphMLAttribute {
  private String attriName;

  private GraphMLExporter.AttributeCategory category;

  private GraphMLExporter.AttributeType type;

  public GraphMLAttribute(
      String attriName,
      GraphMLExporter.AttributeCategory category,
      GraphMLExporter.AttributeType type) {
    this.attriName = attriName;
    this.category = category;
    this.type = type;
  }

  public String getAttriName() {
    return attriName;
  }

  public GraphMLExporter.AttributeCategory getCategory() {
    return category;
  }

  public GraphMLExporter.AttributeType getType() {
    return type;
  }
}
