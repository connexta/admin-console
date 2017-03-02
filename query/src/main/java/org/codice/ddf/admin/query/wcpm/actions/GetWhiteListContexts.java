package org.codice.ddf.admin.query.wcpm.actions;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_CONTEXT_PATH_LIST;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.commons.fields.common.ContextPathList;

public class GetWhiteListContexts extends BaseActionField<ContextPathList> {

    public static final String DEFAULT_FIELD_NAME = "whitelisted";
    public static final String DESCRIPTION = "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

    public GetWhiteListContexts() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPathList());
    }

    @Override
    public ContextPathList process(Map<String, Object> args) {
        return SAMPLE_CONTEXT_PATH_LIST;
    }

    @Override
    public List<Field> getArguments() {
        return null;
    }
}
