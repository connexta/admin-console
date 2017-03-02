package org.codice.ddf.admin.query.sources.common.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.sources.common.SourceConfigUnionField;

public class WfsSourceConfigurationField extends SourceConfigUnionField {

    public static final String FIELD_TYPE_NAME = "WfsSourceConfiguration";
    public static final String DESCRIPTION = "Represents a WFS configuration containing properties to be saved.";

    public WfsSourceConfigurationField() {
        super(FIELD_TYPE_NAME, DESCRIPTION);
        this.endpointUrl.setValue("SampleWfsUrl");
        this.id.setValue("SampleWfsId");
    }

    @Override
    public List<Field> getFields() {
        // TODO: tbatie - 2/28/17 - Add additional fields once implemented
        return super.getFields();
    }

}
