package org.codice.ddf.admin.query.sample;

import java.util.ArrayList;
import java.util.Map;

import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.commons.DefaultActionType;

public class SampleActionType extends DefaultActionType {

    public static final String ACTION_ID = "sampleIdAction";
    public static final String DESCRIPTION = "Sample action for testing purposes only.";

    // TODO: tbatie - 2/6/17 - Req and optional fields
    public SampleActionType() {
        super(ACTION_ID, DESCRIPTION, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public ActionReport process() {
        return new SampleActionReport();
    }

    @Override
    public DefaultActionType setArguments(Map<String, Object> args) {
        // TODO: tbatie - 2/6/17 - set sample fields and cast here
        return null;
    }
}
