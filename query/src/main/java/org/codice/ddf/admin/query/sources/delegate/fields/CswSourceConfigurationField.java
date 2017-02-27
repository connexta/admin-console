package org.codice.ddf.admin.query.sources.delegate.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.UnionValueField;
import org.codice.ddf.admin.query.commons.fields.common.UriField;
import org.codice.ddf.admin.query.sources.common.SourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class CswSourceConfigurationField extends SourceConfigurationField implements UnionValueField {

    public static final String DEFAULT_FIELD_NAME = "cswConfig";
    public static final String FIELD_TYPE_NAME = "CswSourceConfiguration";
    public static final String DESCRIPTION = "Represents a CSW configuration containing properties to be saved.";
    private UriField url;

    public CswSourceConfigurationField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.url = new UriField();
        this.endpointUrl.setValue("cswUrl");
        this.id.setValue("cswId");
    }

    @Override
    public List<Field> getFields() {
        return new ImmutableList.Builder<Field>().addAll(super.getFields())
                .add(url)
                .build();
    }
}
