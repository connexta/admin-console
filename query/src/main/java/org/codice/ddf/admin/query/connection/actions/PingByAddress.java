package org.codice.ddf.admin.query.connection.actions;

import static org.codice.ddf.admin.query.commons.sample.SampleFields.SAMPLE_REPORT;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.AddressField;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;

import com.google.common.collect.ImmutableList;

public class PingByAddress extends TestAction {
    public static final String NAME = "pingByAddress";
    public static final String DESCRIPTION = "Attempts to reach the given address";

    private AddressField address;

    public PingByAddress() {
        super(NAME, DESCRIPTION);
        address = new AddressField();
    }

    @Override
    public ReportField process() {
        return SAMPLE_REPORT;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(address);
    }
}
