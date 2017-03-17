package org.codice.ddf.admin.query.wcpm.actions.discover;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_CONTEXT_PATHS;

import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.commons.fields.common.ContextPaths;

public class GetWhiteListContexts extends GetAction<ContextPaths> {

    public static final String DEFAULT_FIELD_NAME = "whitelisted";
    public static final String DESCRIPTION = "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

    public GetWhiteListContexts() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPaths());
    }

    @Override
    public ContextPaths process() {
        return SAMPLE_CONTEXT_PATHS;
    }

}
