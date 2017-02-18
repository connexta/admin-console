package org.codice.ddf.admin.query.sample.fields;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class SampleStringField extends StringField {

    public static final String SAMPLE_FIELD_NAME = "sampleFieldName";
    protected SampleStringField() {
        super(SAMPLE_FIELD_NAME);
    }
}
