package org.codice.ddf.admin.query.wcpm.actions.persist;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_CONTEXT_PATHS;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.commons.fields.common.ContextPaths;

import com.google.common.collect.ImmutableList;

public class SaveWhitelistedContexts extends BaseActionField<ContextPaths>{

    public static final String DEFAULT_FIELD_NAME = "saveWhitelistContexts";
    public static final String DESCRIPTION = "Persists the given contexts paths as white listed contexts. White listing a context path will result in no security being applied to the given paths.";
    private ContextPaths contexts;

    public SaveWhitelistedContexts() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPaths());
        contexts = new ContextPaths();
    }

    @Override
    public ContextPaths process(Map args) {
        return SAMPLE_CONTEXT_PATHS;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(contexts);
    }
}
