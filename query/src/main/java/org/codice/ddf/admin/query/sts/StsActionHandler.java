package org.codice.ddf.admin.query.sts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.BaseActionHandler;
import org.codice.ddf.admin.query.sts.actions.GetStsClaimsAction;

public class StsActionHandler extends BaseActionHandler {

    public static final String FIELD_NAME = "sts";
    public static final String FIELD_TYPE_NAME = "SecurityTokenService";
    public static final String DESCRIPTION = "The STS (Security Token Service) is responsible for generating assertions that allow clients to be authenticated.";

    public StsActionHandler() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return Arrays.asList(new GetStsClaimsAction());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return new ArrayList<>();
    }
}
