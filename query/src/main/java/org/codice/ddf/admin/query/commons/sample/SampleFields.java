package org.codice.ddf.admin.query.commons.sample;

import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;

public class SampleFields {

    public static final MessageField SAMPLE_SUCCESS_MSG = new SuccessMessageField("SAMPLE_SUCCESS_CODE", "Sample success msg.");
    public static final MessageField SAMPLE_WARNING_MSG = new WarningMessageField("SAMPLE_WARNING_CODE", "Sample warning msg.");
    public static final MessageField SAMPLE_FAILURE_MSG = new FailureMessageField("SAMPLE_FAILURE_CODE", "Sample failure msg.");
    public static final ReportField SAMPLE_REPORT = new ReportField().messages(SAMPLE_SUCCESS_MSG, SAMPLE_WARNING_MSG, SAMPLE_FAILURE_MSG);
}
