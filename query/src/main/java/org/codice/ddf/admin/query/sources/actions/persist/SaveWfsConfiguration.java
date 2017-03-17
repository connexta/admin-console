package org.codice.ddf.admin.query.sources.actions.persist;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_WFS_SOURCE_INFO;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.sources.common.SourceInfoField;
import org.codice.ddf.admin.query.sources.common.fields.WfsSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveWfsConfiguration extends BaseAction<SourceInfoField> {

    public static final String NAME = "saveWfsSource";
    public static final String DESCRIPTION = "Saves a wfs source configuration. If a pid is specified, that source configuration will be updated.";
    private WfsSourceConfigurationField config;

    public SaveWfsConfiguration() {
        super(NAME, DESCRIPTION, new SourceInfoField());
        config = new WfsSourceConfigurationField();
    }

    @Override
    public SourceInfoField process() {
        return SAMPLE_WFS_SOURCE_INFO;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
