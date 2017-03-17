package org.codice.ddf.admin.query.sts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.action.Action;
import org.codice.ddf.admin.query.commons.actions.BaseActionCreator;
import org.codice.ddf.admin.query.sts.actions.GetStsClaimsAction;

public class StsActionHandler extends BaseActionCreator {

    public static final String NAME = "sts";
    public static final String TYPE_NAME = "SecurityTokenService";
    public static final String DESCRIPTION = "The STS (Security Token Service) is responsible for generating assertions that allow clients to be authenticated.";

    public StsActionHandler() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return Arrays.asList(new GetStsClaimsAction());
    }

    @Override
    public List<Action> getPersistActions() {
        return new ArrayList<>();
    }
}
