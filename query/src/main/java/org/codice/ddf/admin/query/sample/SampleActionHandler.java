package org.codice.ddf.admin.query.sample;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.ActionType;
import org.codice.ddf.admin.query.api.Field;
import org.codice.ddf.admin.query.commons.DefaultActionHandler;

public class SampleActionHandler extends DefaultActionHandler {
    @Override
    public String description() {
        return null;
    }

    @Override
    public ActionReport process(ActionType action, List<Field> args) {
        return null;
    }

    @Override
    public List<ActionType> getSupportedActions() {
        return null;
    }
    //    @Override
//    public String description() {
//        return "Sample action handler for testing purposes";
//    }
//
//    @Override
//    public List<ActionType> getSupportedActions() {
//        return Arrays.asList(new SampleActionType());
//    }
}
