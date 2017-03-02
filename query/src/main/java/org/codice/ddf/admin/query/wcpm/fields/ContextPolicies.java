package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class ContextPolicies extends BaseListField<ContextPolicyBin> {

    public static final String DEFAULT_FIELD_NAME = "policies";
    public static final String DESCRIPTION = "A list of polices being applied to a collection of context paths";

    public ContextPolicies() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public ContextPolicies addField(ContextPolicyBin value) {
        super.addField(value);
        return this;
    }

    @Override
    public Field getListValueField() {
        return new ContextPolicyBin();
    }
}
