package org.codice.ddf.admin.query.sources.actions.persist;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_SOURCES_INFO_LIST;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.sources.common.SourceInfoListField;

import com.google.common.collect.ImmutableList;

public class DeleteSource extends BaseAction<SourceInfoListField> {

    public static final String NAME = "deleteSource";
    public static final String DESCRIPTION = "Delete's the given pid of a source. Returns back a list of all currently configured sources";
    private PidField pid;

    public DeleteSource() {
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
