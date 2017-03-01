package org.codice.ddf.admin.query.sources.actions.discover;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_SOURCES_INFO_LIST;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.commons.fields.common.UrlField;
import org.codice.ddf.admin.query.sources.common.SourceInfoListField;

import com.google.common.collect.ImmutableList;

public class DiscoverSourceByUrlAction extends BaseActionField<SourceInfoListField> {

    public static final String FIELD_NAME = "discoverByUrl";
    public static final String DESCRIPTION = "Attempts to discover source given a url.";

    private UrlField endpoint = new UrlField();

    public DiscoverSourceByUrlAction() {
        super(FIELD_NAME, DESCRIPTION, new SourceInfoListField());
    }

    @Override
    public SourceInfoListField process(Map<String, Object> args) {
        return SAMPLE_SOURCES_INFO_LIST;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(endpoint);
    }
}