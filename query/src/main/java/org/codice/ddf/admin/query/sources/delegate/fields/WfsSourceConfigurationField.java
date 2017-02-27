package org.codice.ddf.admin.query.sources.delegate.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.UnionValueField;
import org.codice.ddf.admin.query.sources.common.SourceConfigurationField;

public class WfsSourceConfigurationField extends SourceConfigurationField implements UnionValueField {

    public static final String DEFAULT_FIELD_NAME = "wfsConfig";
    public static final String FIELD_TYPE_NAME = "WfsSourceConfiguration";
    public static final String DESCRIPTION = "Represents a WFS configuration containing properties to be saved.";

    public WfsSourceConfigurationField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.endpointUrl.setValue("wfsUrl");
        this.id.setValue("wfsId");
    }

    @Override
    public List<Field> getFields() {
        return super.getFields();
    }
}
