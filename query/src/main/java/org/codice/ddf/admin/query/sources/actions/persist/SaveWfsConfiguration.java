package org.codice.ddf.admin.query.sources.actions.persist;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_CSW_CONFIG;
import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_WFS_CONFIG;
import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_WFS_SOURCE_INFO;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.sources.common.SourceInfoField;
import org.codice.ddf.admin.query.sources.common.fields.CswSourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.fields.WfsSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveWfsConfiguration extends BaseActionField<SourceInfoField> {

    public static final String FIELD_NAME = "saveWfsSource";
    public static final String DESCRIPTION = "Saves a wfs source configuration. If a pid is specified, that source configuration will be updated.";
    private WfsSourceConfigurationField config;

    public SaveWfsConfiguration() {
        super(FIELD_NAME, DESCRIPTION, new SourceInfoField());
        config = new WfsSourceConfigurationField();
    }

    @Override
    public SourceInfoField process(Map args) {
        return SAMPLE_WFS_SOURCE_INFO;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
