package org.codice.ddf.admin.query.commons.sample;

import org.codice.ddf.admin.query.commons.fields.common.BaseReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.BaseMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;

/**
 * Created by tbatie1 on 2/28/17.
 */
public class SampleFields {

    public static final BaseMessageField SAMPLE_SUCCESS_MSG = new SuccessMessageField("SAMPLE_SUCCESS_CODE", "Sample success msg.");
    public static final BaseMessageField SAMPLE_WARNING_MSG = new WarningMessageField("SAMPLE_WARNING_CODE", "Sample warning msg.");
    public static final BaseMessageField SAMPLE_FAILURE_MSG = new FailureMessageField("SAMPLE_FAILURE_CODE", "Sample failure msg.");
    public static final BaseReportField SAMPLE_REPORT = new BaseReportField().messages(SAMPLE_SUCCESS_MSG, SAMPLE_WARNING_MSG, SAMPLE_FAILURE_MSG);
}
