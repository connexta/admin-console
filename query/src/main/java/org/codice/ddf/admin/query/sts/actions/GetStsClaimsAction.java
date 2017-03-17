package org.codice.ddf.admin.query.sts.actions;

import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.sts.field.StsClaimField;
import org.codice.ddf.admin.query.sts.field.StsClaimsField;

public class GetStsClaimsAction extends GetAction<StsClaimsField> {

    public static final String NAME = "claims";
    public static final String DESCRIPTION = "All currently configured claims the STS supports.";

    public GetStsClaimsAction() {
        super(NAME, DESCRIPTION, new StsClaimsField());
    }

    @Override
    public StsClaimsField process() {
        StsClaimField claim = new StsClaimField();
        claim.setValue("testClaim");
        return new StsClaimsField().add(claim);
    }
}
