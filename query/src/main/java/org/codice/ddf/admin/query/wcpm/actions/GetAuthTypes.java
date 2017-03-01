package org.codice.ddf.admin.query.wcpm.actions;

import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.wcpm.fields.AuthType;
import org.codice.ddf.admin.query.wcpm.fields.AuthTypeList;

public class GetAuthTypes extends GetAction<AuthTypeList> {

    public static final String FIELD_NAME = "authTypes";
    public static final String DESCRIPTION = "Retrieves all currently configured authentication types.";

    public GetAuthTypes() {
        super(FIELD_NAME, DESCRIPTION, new AuthTypeList());
    }

    @Override
    public AuthTypeList process() {
        AuthTypeList authTypes = new AuthTypeList();
        authTypes.addField(AuthType.BASIC_AUTH);
        authTypes.addField(AuthType.IDP_AUTH);
        authTypes.addField(AuthType.PKI_AUTH);
        authTypes.addField(AuthType.SAML_AUTH);
        authTypes.addField(AuthType.GUEST_AUTH);
        return authTypes;
    }
}
