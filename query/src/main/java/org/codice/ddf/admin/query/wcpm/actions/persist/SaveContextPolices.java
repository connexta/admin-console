package org.codice.ddf.admin.query.wcpm.actions.persist;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_CONTEXT_POLICES;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.wcpm.fields.ContextPolicies;

import com.google.common.collect.ImmutableList;

public class SaveContextPolices extends BaseActionField<ContextPolicies> {

    public static final String DEFAULT_FIELD_NAME = "saveContextPolicies";
    public static final String DESCRIPTION = "Saves a list of policies to be applied to their corresponding context paths.";
    private ContextPolicies contextPolicies;

    public SaveContextPolices() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPolicies());
        contextPolicies = new ContextPolicies();
    }

    @Override
    public ContextPolicies process(Map args) {
        return SAMPLE_CONTEXT_POLICES;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(contextPolicies);
    }
}
