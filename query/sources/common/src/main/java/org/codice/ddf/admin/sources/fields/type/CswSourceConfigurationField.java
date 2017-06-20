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

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.sources.fields.CswOutputSchemaField;
import org.codice.ddf.admin.sources.fields.CswProfile;
import org.codice.ddf.admin.sources.fields.CswSpatialOperator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class CswSourceConfigurationField extends SourceConfigField {

    public static final String DEFAULT_FIELD_NAME = "source";

    public static final String FIELD_TYPE_NAME = "CswSourceConfiguration";

    public static final String DESCRIPTION =
            "Represents a CSW configuration containing properties to be saved. If specified, the spatial operator applies "
                    + "the specific operator to the records returned by a GetRecords request, and the output schema defines the schema of the records returned "
                    + "by the request.";

    public static final String OUTPUT_SCHEMA_FIELD_NAME = CswOutputSchemaField.DEFAULT_FIELD_NAME;

    public static final String CSW_PROFILE_FIELD_NAME = CswProfile.DEFAULT_FIELD_NAME;

    public static final String SPATIAL_OPERATOR_FIELD_NAME = CswSpatialOperator.DEFAULT_FIELD_NAME;

    private CswOutputSchemaField outputSchema;

    private CswSpatialOperator spatialOperator;

    private CswProfile cswProfile;

    public CswSourceConfigurationField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        outputSchema = new CswOutputSchemaField(OUTPUT_SCHEMA_FIELD_NAME);
        cswProfile = new CswProfile();
        spatialOperator = new CswSpatialOperator();
        updateInnerFieldPaths();
    }

    public CswSourceConfigurationField outputSchema(String outputSchema) {
        this.outputSchema.setValue(outputSchema);
        return this;
    }

    public CswSourceConfigurationField spatialOperator(String spatialOperator) {
        this.spatialOperator.setValue(spatialOperator);
        return this;
    }

    public CswSourceConfigurationField cswProfile(String cswProfile) {
        this.cswProfile.setValue(cswProfile);
        return this;
    }

    public String outputSchema() {
        return outputSchema.getValue();
    }

    public CswProfile cswProfileField() {
        return cswProfile;
    }

    public StringField outputSchemaField() {
        return outputSchema;
    }

    public String spatialOperator() {
        return spatialOperator.getValue();
    }

    public CswSpatialOperator spatialOperatorField() {
        return spatialOperator;
    }

    public String cswProfile() {
        return cswProfile.getValue();
    }

    public CswSourceConfigurationField useDefaultRequired() {
        isRequired(true);
        cswProfile.isRequired(true);
        sourceNameField().isRequired(true);
        endpointUrlField().isRequired(true);
        return this;
    }

    @Override
    public CswSourceConfigurationField allFieldsRequired(boolean required) {
        super.allFieldsRequired(required);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return new ImmutableList.Builder<Field>().addAll(super.getFields())
                .add(outputSchema)
                .add(cswProfile)
                .add(spatialOperator)
                .build();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add(OUTPUT_SCHEMA_FIELD_NAME, outputSchema())
                .add(CSW_PROFILE_FIELD_NAME, cswProfile())
                .add(SPATIAL_OPERATOR_FIELD_NAME, spatialOperator())
                .addValue(super.toString())
                .toString();
    }
}
