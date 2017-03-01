package org.codice.ddf.admin.query.sources.common.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.UnionValueField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;
import org.codice.ddf.admin.query.sources.common.SourceConfigUnionField;

import com.google.common.collect.ImmutableList;

public class CswSourceConfigurationField extends SourceConfigUnionField implements UnionValueField {

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
