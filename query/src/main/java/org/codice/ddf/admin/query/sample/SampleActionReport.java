package org.codice.ddf.admin.query.sample;

import java.util.List;

import org.codice.ddf.admin.query.api.ActionMessage;
import org.codice.ddf.admin.query.api.Field;
import org.codice.ddf.admin.query.commons.DefaultActionReport;

public class SampleActionReport extends DefaultActionReport {

    private List<Field> values;
    private List<Field> returnTypes;

}
