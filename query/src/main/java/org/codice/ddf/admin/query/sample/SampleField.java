package org.codice.ddf.admin.query.sample;

import org.codice.ddf.admin.query.commons.CommonFields;

public class SampleField extends CommonFields.StringField {
    @Override
    public String getUniqueName() {
        return "sampleField";
    }

    @Override
    public String getDescription() {
        return "Sample field for testing purposes only.";
    }
}
