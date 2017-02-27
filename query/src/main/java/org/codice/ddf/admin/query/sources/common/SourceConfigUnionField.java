package org.codice.ddf.admin.query.sources.common;

import java.util.Arrays;

import org.codice.ddf.admin.query.commons.fields.base.BaseUnionField;
import org.codice.ddf.admin.query.sources.delegate.fields.CswSourceConfigurationField;
import org.codice.ddf.admin.query.sources.delegate.fields.WfsSourceConfigurationField;

public class SourceConfigUnionField extends BaseUnionField {

    public static final String FIELD_TYPE_NAME = "SourceConfigurationUnion";
    public static final String DESCRIPTION = "All supported source configuration types";

    public SourceConfigUnionField() {
        super(FIELD_TYPE_NAME, DESCRIPTION, Arrays.asList(new CswSourceConfigurationField(), new WfsSourceConfigurationField()));
    }
}
