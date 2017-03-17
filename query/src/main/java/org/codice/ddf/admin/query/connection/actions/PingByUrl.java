package org.codice.ddf.admin.query.connection.actions;

import static org.codice.ddf.admin.query.commons.sample.SampleFields.SAMPLE_REPORT;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.UrlField;

import com.google.common.collect.ImmutableList;

public class PingByUrl extends TestAction {

    public static final String NAME = "pingByUrl";
    public static final String DESCRIPTION = "Attempts to reach the given url.";

    private UrlField url;

    public PingByUrl() {
        super(NAME, DESCRIPTION);
        url = new UrlField();
    }

    public ReportField process() {
        return SAMPLE_REPORT;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(url);
    }

}