package org.codice.ddf.admin.query.sts.actions;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.action.GetAction;

public class GetStsClaimsAction extends GetAction {

    public static final String ACTION_ID = "claims";
    public static final String DESCRIPTION = "Retrieves the currently configured claims for the STS supports.";

    public GetStsClaimsAction(Field returnType) {
        super(ACTION_ID, DESCRIPTION, returnType);
    }

    @Override
    public Field process() {
        return null;
    }
}
