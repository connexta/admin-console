package org.codice.ddf.admin.query.sample.actions;

import org.codice.ddf.admin.query.api.fields.Report;
import org.codice.ddf.admin.query.commons.actions.GetAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;

public class SampleAction extends GetAction<Report> {

    public static final String ACTION_ID = "sampleAction";
    public static final String DESCRIPTION = "Sample action for testing purposes only.";

    public SampleAction() {
        super(ACTION_ID, DESCRIPTION, new ReportField());
    }

    @Override
    public ReportField process() {
        MessageField SUCCESS_SAMPLE_MSG_1 = new SuccessMessageField("PASS", "First success message");
        MessageField SUCCESS_SAMPLE_MSG_2 = new SuccessMessageField("PASS_2", "Second success message");

        MessageField WARNING_SAMPLE_MSG_1 = new WarningMessageField("WARN", "First warning message");
        MessageField WARNING_SAMPLE_MSG_2 = new WarningMessageField("WARN_2", "Second warning message");

        MessageField FAILURE_SAMPLE_MSG_1 = new FailureMessageField("FAIL", "First fail message");
        MessageField FAILURE_SAMPLE_MSG_2 = new FailureMessageField("FAIL_2", "Second fail message");

        return new ReportField().messages(SUCCESS_SAMPLE_MSG_1,
                SUCCESS_SAMPLE_MSG_2,
                WARNING_SAMPLE_MSG_1,
                WARNING_SAMPLE_MSG_2,
                FAILURE_SAMPLE_MSG_1,
                FAILURE_SAMPLE_MSG_2);
    }

}
