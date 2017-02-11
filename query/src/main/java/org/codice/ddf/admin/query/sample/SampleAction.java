package org.codice.ddf.admin.query.sample;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.ActionMessage;
import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.DefaultActionReport;
import org.codice.ddf.admin.query.commons.action.GetAction;
import org.codice.ddf.admin.query.commons.message.FailureMessage;
import org.codice.ddf.admin.query.commons.message.SuccessMessage;
import org.codice.ddf.admin.query.commons.message.WarningMessage;

public class SampleAction extends GetAction {

    public static final String ACTION_ID = "sampleAction";
    public static final String DESCRIPTION = "Sample action for testing purposes only.";

    private static final List<Field> RETURN_TYPES = Arrays.asList(new SampleFields.SampleStringField());

    // TODO: tbatie - 2/6/17 - Req and optional fields
    public SampleAction() {
        super(ACTION_ID, DESCRIPTION);
    }

    @Override
    public ActionReport process() {
        SuccessMessage SUCCESS_SAMPLE_MSG_1 = new SuccessMessage("PASS", "First success message");
        SuccessMessage SUCCESS_SAMPLE_MSG_2 = new SuccessMessage("PASS_2", "Second success message");

        WarningMessage WARNING_SAMPLE_MSG_1 = new WarningMessage("WARN", "First warning message");
        WarningMessage WARNING_SAMPLE_MSG_2 = new WarningMessage("WARN_2", "Second warning message");

        FailureMessage FAILURE_SAMPLE_MSG_1 = new FailureMessage("FAIL", "First fail message");
        FailureMessage FAILURE_SAMPLE_MSG_2 = new FailureMessage("FAIL_2", "Second fail message");

        ActionMessage[] messages = new ActionMessage[]{SUCCESS_SAMPLE_MSG_1, SUCCESS_SAMPLE_MSG_2, WARNING_SAMPLE_MSG_1, WARNING_SAMPLE_MSG_2, FAILURE_SAMPLE_MSG_1, FAILURE_SAMPLE_MSG_2};
        return new DefaultActionReport().messages(messages).addValue(SampleFields.SampleStringField.SAMPLE_FIELD_NAME,
                "sampleFieldValue");
    }

    @Override
    public List<Field> getReturnTypes() {
        return RETURN_TYPES;
    }
}
