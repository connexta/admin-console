package org.codice.ddf.admin.query.wcpm;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.BaseActionHandler;
import org.codice.ddf.admin.query.wcpm.actions.GetAuthTypes;

import com.google.common.collect.ImmutableList;

public class WCPMActionHandler extends BaseActionHandler {
    public static final String FIELD_NAME = "wcpm";
    public static final String FIELD_TYPE_NAME = "WebContextPolicyManager";
    public static final String DESCRIPTION = "Manages policies for the system's endpoints";

    public WCPMActionHandler() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return ImmutableList.of(new GetAuthTypes());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return ImmutableList.of();
    }
}
