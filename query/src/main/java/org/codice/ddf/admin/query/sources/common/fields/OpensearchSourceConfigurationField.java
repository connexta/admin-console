package org.codice.ddf.admin.query.sources.common.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.sources.common.SourceConfigUnionField;

public class OpensearchSourceConfigurationField extends SourceConfigUnionField {

    private static final String FIELD_TYPE_NAME = "OpenSearchConfiguration";
    private static final String DESCRIPTION = "Example opensearch config description";

    public OpensearchSourceConfigurationField() {
        super(FIELD_TYPE_NAME, DESCRIPTION);
    }
    @Override
    public List<Field> getFields() {
        // TODO: tbatie - 2/28/17 - Add additional fields once implemented
        return super.getFields();
    }
}
