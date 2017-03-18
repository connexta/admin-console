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

import com.google.common.collect.ImmutableList;

public class CswSourceConfigurationField extends SourceConfigUnionField {

    public static final String FIELD_TYPE_NAME = "CswSourceConfiguration";
    public static final String DESCRIPTION = "Represents a CSW configuration containing properties to be saved.";

    private StringField outputSchema = new StringField("outputSchema");
    private StringField forceSpatialFilter = new StringField("forceSpatialFilter");

    public CswSourceConfigurationField() {
        super(FIELD_TYPE_NAME, DESCRIPTION);
        this.endpointUrl.setValue("SampleCswUrl");
        this.id.setValue("SampleCswId");
    }

    public CswSourceConfigurationField outputSchema(String outputSchema) {
        this.outputSchema.setValue(outputSchema);
        return this;
    }

    public CswSourceConfigurationField forceSpatialFilter(String forceSpatialFilter) {
        this.forceSpatialFilter.setValue(forceSpatialFilter);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return new ImmutableList.Builder<Field>().addAll(super.getFields())
                .add(outputSchema)
                .add(forceSpatialFilter)
                .build();
    }
}
