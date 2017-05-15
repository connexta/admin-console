/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.sources.fields;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class CswSpatialOperator extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "cswSpatialOperator";

    public static final String TYPE_NAME = "CswSpatialOperator";

    public static final String DESCRIPTION =
            "A spatial operator determines whether its geometric arguments satisfy the stated spatial relationship.";

    public static final String NO_FILTER_FIELD_NAME = "None";

    public static final String BBOX_FIELD_NAME = "BBOX";

    public static final String BEYOND_FIELD_NAME = "Beyond";

    public static final String CONTAINS_FIELD_NAME = "Contains";

    public static final String CROSSES_FIELD_NAME = "Crosses";

    public static final String DISJOINT_FIELD_NAME = "Disjoint";

    public static final String DWITHIN_FIELD_NAME = "DWithin";

    public static final String EQUALS_FIELD_NAME = "Equals";

    public static final String INTERSECTS_FIELD_NAME = "Intersects";

    public static final String OVERLAPS_FIELD_NAME = "Overlaps";

    public static final String TOUCHES_FIELD_NAME = "Touches";

    public static final String WITHIN_FIELD_NAME = "Within";

    public CswSpatialOperator() {
        this(new NoFilter());
    }

    public CswSpatialOperator(Field<String> spatialOperator) {
        super(DEFAULT_FIELD_NAME,
                TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new NoFilter(),
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

    protected static final class NoFilter extends StringField {

        public static final String NO_FILTER = "NO_FILTER";

        public static final String DESCRIPTION =
                "Indicates that no spatial operators should be applied.";

        public NoFilter() {
            super(NO_FILTER_FIELD_NAME, NO_FILTER_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return NO_FILTER;
        }
    }

    protected static final class Bbox extends StringField {

        public static final String BBOX = "BBOX";

        public static final String DESCRIPTION =
                "Identifies all geometries that spatially interact with a bounding box.";

        public Bbox() {
            super(BBOX_FIELD_NAME, BBOX_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return BBOX;
        }
    }

    protected static final class Beyond extends StringField {

        public static final String BEYOND = "Beyond";

        public static final String DESCRIPTION =
                "Tests whether the value of a geometric property A is beyond a specified distance d of the specified literal geometric value B.";

        public Beyond() {
            super(BEYOND_FIELD_NAME, BEYOND_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return BEYOND;
        }
    }

    protected static final class Contains extends StringField {

        public static final String CONTAINS = "Contains";

        public static final String DESCRIPTION =
                "Determines whether the second geometry is completely within the first geometry. Contain tests the exact opposite result of within.";

        public Contains() {
            super(CONTAINS_FIELD_NAME, CONTAINS_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return CONTAINS;
        }
    }

    protected static final class Crosses extends StringField {

        public static final String CROSSES = "Crosses";

        public static final String DESCRIPTION =
                "Determines whether two geometric properties cross each other.";

        public Crosses() {
            super(CROSSES_FIELD_NAME, CROSSES_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return CROSSES;
        }
    }

    protected static final class Disjoint extends StringField {

        public static final String DISJOINT = "Disjoint";

        public static final String DESCRIPTION =
                "Determines whether two geometric properties do not intersect.";

        public Disjoint() {
            super(DISJOINT_FIELD_NAME, DISJOINT_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return DISJOINT;
        }
    }

    protected static final class DWithin extends StringField {

        public static final String DWITHIN = "DWithin";

        public static final String DESCRIPTION =
                "Determines whether the value of a geometric property A is within a specified distance d of the specified literal geometric value B.";

        public DWithin() {
            super(DWITHIN_FIELD_NAME, DWITHIN_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return DWITHIN;
        }
    }

    protected static final class Equals extends StringField {

        public static final String EQUALS = "Equals";

        public static final String DESCRIPTION =
                "Determines whether two geometric properties are identical.";

        public Equals() {
            super(EQUALS_FIELD_NAME, EQUALS_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return EQUALS;
        }
    }

    protected static final class Intersects extends StringField {

        public static final String INTERSECTS = "Intersects";

        public static final String DESCRIPTION =
                "Determines whether two geometric properties intersect with each other.";

        public Intersects() {
            super(INTERSECTS_FIELD_NAME, INTERSECTS_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return INTERSECTS;
        }
    }

    protected static final class Overlaps extends StringField {

        public static final String OVERLAPS = "Overlaps";

        public static final String DESCRIPTION =
                "Determines whether two geometries of the same dimensions overlap. If their intersection produces a geometry different from both and of the same dimension, they overlap.";

        public Overlaps() {
            super(OVERLAPS_FIELD_NAME, OVERLAPS_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return OVERLAPS;
        }
    }

    protected static final class Touches extends StringField {

        public static final String TOUCHES = "Touches";

        public static final String DESCRIPTION =
                "Determines if the points of two geometric properties touch, but do not intersect the interiors of each geometry.";

        public Touches() {
            super(TOUCHES_FIELD_NAME, TOUCHES_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return TOUCHES;
        }
    }

    protected static final class Within extends StringField {

        public static final String WITHIN = "Within";

        public static final String DESCRIPTION =
                "Determines whether the first geometry is completely within the second geometry. Within tests the exact opposite result of contains.";

        public Within() {
            super(WITHIN_FIELD_NAME, WITHIN_FIELD_NAME, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return WITHIN;
        }
    }
}
