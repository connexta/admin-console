package org.codice.ddf.admin.query.sources.actions.discover;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_SOURCES_INFO_LIST;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.sources.common.SourceInfoListField;

import com.google.common.collect.ImmutableList;

public class GetSourceConfigsAction extends BaseAction<SourceInfoListField> {

    public static final String NAME = "configs";
    public static final String DESCRIPTION = "Retrieves all currently configured sources. If a source pid is specified, only that source configuration will be returned.";
    public PidField pid;

    public GetSourceConfigsAction() {
        super(NAME, DESCRIPTION, new SourceInfoListField());
        pid = new PidField();
    }

    @Override
    public SourceInfoListField process() {
        return SAMPLE_SOURCES_INFO_LIST;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(pid);
    }
}
