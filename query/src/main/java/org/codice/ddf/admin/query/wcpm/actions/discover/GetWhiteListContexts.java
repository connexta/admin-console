package org.codice.ddf.admin.query.wcpm.actions.discover;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_CONTEXT_PATHS;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.commons.fields.common.ContextPaths;

public class GetWhiteListContexts extends BaseActionField<ContextPaths> {

    public static final String DEFAULT_FIELD_NAME = "whitelisted";
    public static final String DESCRIPTION = "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

    public GetWhiteListContexts() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPaths());
    }

    @Override
    public ContextPaths process(Map<String, Object> args) {
        return SAMPLE_CONTEXT_PATHS;
    }

    @Override
    public List<Field> getArguments() {
        return null;
    }
}
