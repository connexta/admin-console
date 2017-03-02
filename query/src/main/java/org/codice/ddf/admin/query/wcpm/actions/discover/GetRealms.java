package org.codice.ddf.admin.query.wcpm.actions.discover;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_REALM_LIST;

import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.wcpm.fields.RealmList;

public class GetRealms extends GetAction<RealmList> {

    public static final String FIELD_NAME = "realms";
    public static final String DESCRIPTION = "Retrieves all currently configured realms.";

    public GetRealms() {
        super(FIELD_NAME, DESCRIPTION, new RealmList());
    }

    @Override
    public RealmList process() {
        return SAMPLE_REALM_LIST;
    }
}
