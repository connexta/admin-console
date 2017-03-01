package org.codice.ddf.admin.query.connection.actions;

import static org.codice.ddf.admin.query.commons.sample.SampleFields.SAMPLE_REPORT;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ReportField;
import org.codice.ddf.admin.query.commons.actions.TestActionField;
import org.codice.ddf.admin.query.commons.fields.common.UrlField;

import com.google.common.collect.ImmutableList;

public class PingByUrl extends TestActionField {
    public static final String FIELD_NAME = "pingByUrl";
    public static final String DESCRIPTION = "Attempts to reach the given url.";
    private UrlField url;

    public PingByUrl() {
        super(FIELD_NAME, DESCRIPTION);
        url = new UrlField();
    }

    @Override
    public ReportField process(Map<String, Object> args) {
        return SAMPLE_REPORT;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(url);
    }
}