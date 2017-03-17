package org.codice.ddf.admin.query.sources.actions.discover;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_SOURCES_INFO_LIST;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.commons.fields.common.UrlField;
import org.codice.ddf.admin.query.sources.common.SourceInfoListField;

import com.google.common.collect.ImmutableList;

public class DiscoverSourceByUrlAction extends BaseAction<SourceInfoListField> {

    public static final String NAME = "discoverByUrl";
    public static final String DESCRIPTION = "Attempts to discover source given a url.";

    private UrlField endpoint = new UrlField();

    public DiscoverSourceByUrlAction() {
        super(NAME, DESCRIPTION, new SourceInfoListField());
    }

    @Override
    public SourceInfoListField process() {
        return SAMPLE_SOURCES_INFO_LIST;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(endpoint);
    }
}