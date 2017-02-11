package org.codice.ddf.admin.query.sample;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.ActionHandlerFields;
import org.codice.ddf.admin.query.api.Field;
import org.codice.ddf.admin.query.commons.DefaultFields;

public class SampleFields implements ActionHandlerFields {

    @Override
    public List<Field> allFields() {
        return Arrays.asList(new SampleStringField());
    }

    public static class SampleStringField extends DefaultFields.StringField {

        public static final String SAMPLE_FIELD_NAME = "sampleFieldName";
        protected SampleStringField() {
            super(SAMPLE_FIELD_NAME, "sampleFieldType");
        }
    }

}
