package org.codice.ddf.admin.query.wcpm.actions.discover;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_CONTEXT_POLICES;

import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.wcpm.fields.ContextPolicies;

public class GetContextPolicies extends GetAction<ContextPolicies> {

    public static final String DEFAULT_FIELD_NAME = "policies";
    public static final String DESCRIPTION = "Returns all currently configured policies applied to context paths.";

    public GetContextPolicies() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPolicies());
    }

    @Override
    public ContextPolicies process() {
        return SAMPLE_CONTEXT_POLICES;
    }
}
