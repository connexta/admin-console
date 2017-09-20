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
package org.codice.ddf.admin.sources.fields;

import com.google.common.collect.ImmutableList;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;

public class CswSpatialOperator extends BaseEnumField<String> {

  public static final String DEFAULT_FIELD_NAME = "cswSpatialOperator";

  public static final String TYPE_NAME = "CswSpatialOperator";

  public static final String DESCRIPTION =
      "A spatial operator determines whether its geometric arguments satisfy the stated spatial relationship.";

  public CswSpatialOperator() {
    this(new NoFilter());
  }

  public CswSpatialOperator(EnumValue<String> spatialOperator) {
    super(
        DEFAULT_FIELD_NAME,
        TYPE_NAME,
        DESCRIPTION,
        ImmutableList.of(
            new NoFilter(),
            new Bbox(),
            new Beyond(),
            new Contains(),
            new Crosses(),
            new Disjoint(),
            new DWithin(),
            new Equals(),
            new Intersects(),
            new Overlaps(),
            new Touches(),
            new Within()),
        spatialOperator);
  }

  public static final class NoFilter implements EnumValue<String> {

    public static final String ENUM_TITLE = "None";

    public static final String OPERATOR = "NO_FILTER";

    public static final String DESCRIPTION =
        "Indicates that no spatial operators should be applied.";

    @Override
    public String getEnumTitle() {
      return ENUM_TITLE;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return OPERATOR;
    }
  }

  public static final class Bbox implements EnumValue<String> {

    public static final String BBOX = "BBOX";

    public static final String DESCRIPTION =
        "Identifies all geometries that spatially interact with a bounding box.";

    @Override
    public String getEnumTitle() {
      return BBOX;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return BBOX;
    }
  }

  public static final class Beyond implements EnumValue<String> {

    public static final String BEYOND = "Beyond";

    public static final String DESCRIPTION =
        "Tests whether the value of a geometric property A is beyond a specified distance d of the specified literal geometric value B.";

    @Override
    public String getEnumTitle() {
      return BEYOND;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return BEYOND;
    }
  }

  public static final class Contains implements EnumValue<String> {

    public static final String CONTAINS = "Contains";

    public static final String DESCRIPTION =
        "Determines whether the second geometry is completely within the first geometry. Contain tests the exact opposite result of within.";

    @Override
    public String getEnumTitle() {
      return CONTAINS;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return CONTAINS;
    }
  }

  public static final class Crosses implements EnumValue<String> {

    public static final String CROSSES = "Crosses";

    public static final String DESCRIPTION =
        "Determines whether two geometric properties cross each other.";

    @Override
    public String getEnumTitle() {
      return CROSSES;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return CROSSES;
    }
  }

  public static final class Disjoint implements EnumValue<String> {

    public static final String DISJOINT = "Disjoint";

    public static final String DESCRIPTION =
        "Determines whether two geometric properties do not intersect.";

    @Override
    public String getEnumTitle() {
      return DISJOINT;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return DISJOINT;
    }
  }

  public static final class DWithin implements EnumValue<String> {

    public static final String DWITHIN = "DWithin";

    public static final String DESCRIPTION =
        "Determines whether the value of a geometric property A is within a specified distance d of the specified literal geometric value B.";

    @Override
    public String getEnumTitle() {
      return DWITHIN;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return DWITHIN;
    }
  }

  public static final class Equals implements EnumValue<String> {

    public static final String EQUALS = "Equals";

    public static final String DESCRIPTION =
        "Determines whether two geometric properties are identical.";

    @Override
    public String getEnumTitle() {
      return EQUALS;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return EQUALS;
    }
  }

  public static final class Intersects implements EnumValue<String> {

    public static final String INTERSECTS = "Intersects";

    public static final String DESCRIPTION =
        "Determines whether two geometric properties intersect with each other.";

    @Override
    public String getEnumTitle() {
      return INTERSECTS;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return INTERSECTS;
    }
  }

  public static final class Overlaps implements EnumValue<String> {

    public static final String OVERLAPS = "Overlaps";

    public static final String DESCRIPTION =
        "Determines whether two geometries of the same dimensions overlap. If their intersection produces a geometry different from both and of the same dimension, they overlap.";

    @Override
    public String getEnumTitle() {
      return OVERLAPS;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return OVERLAPS;
    }
  }

  public static final class Touches implements EnumValue<String> {

    public static final String TOUCHES = "Touches";

    public static final String DESCRIPTION =
        "Determines if the points of two geometric properties touch, but do not intersect the interiors of each geometry.";

    @Override
    public String getEnumTitle() {
      return TOUCHES;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return TOUCHES;
    }
  }

  public static final class Within implements EnumValue<String> {

    public static final String WITHIN = "Within";

    public static final String DESCRIPTION =
        "Determines whether the first geometry is completely within the second geometry. Within tests the exact opposite result of contains.";

    @Override
    public String getEnumTitle() {
      return WITHIN;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return WITHIN;
    }
  }
}
