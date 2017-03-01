package org.codice.ddf.admin.query.connection.actions;

import static org.codice.ddf.admin.query.commons.sample.SampleFields.SAMPLE_REPORT;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ReportField;
import org.codice.ddf.admin.query.commons.actions.TestActionField;
import org.codice.ddf.admin.query.commons.fields.common.AddressField;

import com.google.common.collect.ImmutableList;

public class PingByAddress extends TestActionField {
    public static final String FIELD_NAME = "pingByAddress";
    public static final String DESCRIPTION = "Attempts to reach the given address";
    private AddressField address;

    public PingByAddress() {
        super(FIELD_NAME, DESCRIPTION);
        address = new AddressField();
    }

    @Override
    public ReportField process(Map<String, Object> args) {
        return SAMPLE_REPORT;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(address);
    }
}
