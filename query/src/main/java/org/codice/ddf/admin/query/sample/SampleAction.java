package org.codice.ddf.admin.query.sample;

import org.codice.ddf.admin.query.api.field.Message;
import org.codice.ddf.admin.query.api.field.Report;
import org.codice.ddf.admin.query.commons.action.GetAction;
import org.codice.ddf.admin.query.commons.field.BaseFields;

public class SampleAction extends GetAction<Report> {

    public static final String ACTION_ID = "sampleAction";
    public static final String DESCRIPTION = "Sample action for testing purposes only.";

    public SampleAction() {
        super(ACTION_ID, DESCRIPTION, new BaseFields.BaseReport());
    }

    @Override
    public BaseFields.BaseReport process() {
        Message SUCCESS_SAMPLE_MSG_1 = new BaseFields.SuccessMessage("PASS", "First success message");
        Message SUCCESS_SAMPLE_MSG_2 = new BaseFields.SuccessMessage("PASS_2", "Second success message");

        Message WARNING_SAMPLE_MSG_1 = new BaseFields.WarningMessage("WARN", "First warning message");
        Message WARNING_SAMPLE_MSG_2 = new BaseFields.WarningMessage("WARN_2", "Second warning message");

        Message FAILURE_SAMPLE_MSG_1 = new BaseFields.FailureMessage("FAIL", "First fail message");
        Message FAILURE_SAMPLE_MSG_2 = new BaseFields.FailureMessage("FAIL_2", "Second fail message");

        return new BaseFields.BaseReport().messages(SUCCESS_SAMPLE_MSG_1,
                SUCCESS_SAMPLE_MSG_2,
                WARNING_SAMPLE_MSG_1,
                WARNING_SAMPLE_MSG_2,
                FAILURE_SAMPLE_MSG_1,
                FAILURE_SAMPLE_MSG_2);
    }

}
