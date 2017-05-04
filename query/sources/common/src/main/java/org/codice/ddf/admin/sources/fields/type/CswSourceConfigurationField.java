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
 **/
package org.codice.ddf.admin.sources.fields.type;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.services.CswServiceProperties;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class CswSourceConfigurationField extends SourceConfigUnionField {

    public static final String FIELD_TYPE_NAME = "CswSourceConfiguration";

    public static final String DESCRIPTION =
            "Represents a CSW configuration containing properties to be saved.";

    public static final String OUTPUT_SCHEMA = CswServiceProperties.OUTPUT_SCHEMA;

    public static final String FORCED_SPATIAL_FILTER = CswServiceProperties.FORCE_SPATIAL_FILTER;

    public static final String EVENT_SERVICE_ADDRESS = CswServiceProperties.EVENT_SERVICE_ADDRESS;

    private StringField outputSchema;

    private StringField forceSpatialFilter;

    private UrlField eventServiceAddress;

    public CswSourceConfigurationField() {
        super(FIELD_TYPE_NAME, DESCRIPTION);
    }

    public CswSourceConfigurationField outputSchema(String outputSchema) {
        this.outputSchema.setValue(outputSchema);
        return this;
    }

    public CswSourceConfigurationField forceSpatialFilter(String forceSpatialFilter) {
        this.forceSpatialFilter.setValue(forceSpatialFilter);
        return this;
    }

    public CswSourceConfigurationField eventServiceAddress(String url) {
        this.eventServiceAddress.setValue(url);
        return this;
    }

    public String outputSchema() {
        return outputSchema.getValue();
    }

    public StringField outputSchemaField() {
        return outputSchema;
    }

    public String forceSpatialFilter() {
        return forceSpatialFilter.getValue();
    }

    public StringField forceSpatialFilterField() {
        return  forceSpatialFilter;
    }

    public String eventServiceAddress() {
        return eventServiceAddress.getValue();
    }

    public UrlField getEventServiceAddressField() {
        return eventServiceAddress;
    }

    @Override
    public CswSourceConfigurationField allFieldsRequired(boolean required) {
        super.allFieldsRequired(required);
        return this;
    }

    @Override
    public void initializeFields() {
        super.initializeFields();
        outputSchema = new StringField(OUTPUT_SCHEMA);
        forceSpatialFilter = new StringField(FORCED_SPATIAL_FILTER);
        eventServiceAddress = new UrlField(EVENT_SERVICE_ADDRESS);
    }

    @Override
    public List<Field> getFields() {
        return new ImmutableList.Builder<Field>().addAll(super.getFields())
                .add(outputSchema)
                .add(forceSpatialFilter)
                .add(eventServiceAddress)
                .build();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add(OUTPUT_SCHEMA, outputSchema())
                .add(FORCED_SPATIAL_FILTER, forceSpatialFilter())
                .add(EVENT_SERVICE_ADDRESS, eventServiceAddress())
                .addValue(super.toString())
                .toString();
    }
}
