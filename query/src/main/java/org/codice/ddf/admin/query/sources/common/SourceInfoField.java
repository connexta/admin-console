package org.codice.ddf.admin.query.sources.common;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class SourceInfoField extends BaseObjectField {

    private static final String DEFAULT_FIELD_NAME = "sourceInfo";
    private static final String FIELD_TYPE_NAME  = "SourceInfo";
    private static final String DESCRIPTION = "Contains various information such as if the source is reachable, and the source configuration";

    // TODO: tbatie - 2/27/17 - Replace with a boolean scalar once implemented
    private BooleanField isAvailable = new BooleanField("isAvailable");
    private StringField sourceHandlerName = new StringField("sourceHandlerName");
    private SourceConfigUnionField config = new SourceConfigUnionField();

    public SourceInfoField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public SourceInfoField isAvaliable(boolean avaliable) {
        isAvailable.setValue(avaliable);
        return this;
    }

    public SourceInfoField sourceHandlerName(String name) {
        sourceHandlerName.setValue(name);
        return this;
    }

    public SourceInfoField configuration(SourceConfigUnionField config) {
        this.config = config;
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(isAvailable, sourceHandlerName, config);
    }
}
