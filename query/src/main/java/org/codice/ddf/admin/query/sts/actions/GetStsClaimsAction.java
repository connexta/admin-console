package org.codice.ddf.admin.query.sts.actions;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.sts.field.StsClaimField;
import org.codice.ddf.admin.query.sts.field.StsClaimsField;

public class GetStsClaimsAction extends GetAction {

    public static final String ACTION_ID = "claims";
    public static final String DESCRIPTION = "All currently configured claims the STS supports.";

    public GetStsClaimsAction() {
        super(ACTION_ID, DESCRIPTION, new StsClaimsField());
    }

    @Override
    public Field process() {
        return new StsClaimsField().addField(new StsClaimField().setValue("testClaim"));
    }
}
