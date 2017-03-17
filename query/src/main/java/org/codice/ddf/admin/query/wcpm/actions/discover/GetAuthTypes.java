package org.codice.ddf.admin.query.wcpm.actions.discover;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_AUTH_TYPES_LIST;

import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.wcpm.fields.AuthTypeList;

public class GetAuthTypes extends GetAction<AuthTypeList> {

    public static final String FIELD_NAME = "authTypes";
    public static final String DESCRIPTION = "Retrieves all currently configured authentication types.";

    public GetAuthTypes() {
        super(FIELD_NAME, DESCRIPTION, new AuthTypeList());
    }

    @Override
    public AuthTypeList process() {
        return SAMPLE_AUTH_TYPES_LIST;
    }

}
