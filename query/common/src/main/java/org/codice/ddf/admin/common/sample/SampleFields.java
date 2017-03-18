/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.common.sample;

import org.codice.ddf.admin.common.fields.common.ReportField;
import org.codice.ddf.admin.common.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.common.fields.common.message.MessageField;
import org.codice.ddf.admin.common.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.common.fields.common.message.WarningMessageField;

public class SampleFields {

    public static final MessageField SAMPLE_SUCCESS_MSG = new SuccessMessageField("SAMPLE_SUCCESS_CODE", "Sample success msg.");
    public static final MessageField SAMPLE_WARNING_MSG = new WarningMessageField("SAMPLE_WARNING_CODE", "Sample warning msg.");
    public static final MessageField SAMPLE_FAILURE_MSG = new FailureMessageField("SAMPLE_FAILURE_CODE", "Sample failure msg.");
    public static final ReportField SAMPLE_REPORT = new ReportField().messages(SAMPLE_SUCCESS_MSG, SAMPLE_WARNING_MSG, SAMPLE_FAILURE_MSG);
}
