package org.codice.ddf.admin.query.sample;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.field.ActionHandlerFields;
import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.field.BaseFields;

public class SampleFields implements ActionHandlerFields {

    @Override
    public List<Field> allFields() {
        return Arrays.asList(new SampleStringField());
    }

    public static class SampleStringField extends BaseFields.StringField {

        public static final String SAMPLE_FIELD_NAME = "sampleFieldName";
        protected SampleStringField() {
            super(SAMPLE_FIELD_NAME);
        }
    }

}
