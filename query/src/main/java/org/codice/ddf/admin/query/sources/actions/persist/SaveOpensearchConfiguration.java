package org.codice.ddf.admin.query.sources.actions.persist;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_OPENSEARCH_CONFIG;
import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_OPENSEARCH_SOURCE_INFO;
import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_WFS_CONFIG;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.sources.common.SourceInfoField;
import org.codice.ddf.admin.query.sources.common.fields.CswSourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.fields.OpensearchSourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.fields.WfsSourceConfigurationField;

import com.google.common.collect.ImmutableList;

/**
 * Created by tbatie1 on 2/28/17.
 */
public class SaveOpensearchConfiguration extends BaseActionField<SourceInfoField> {

    public static final String FIELD_NAME = "saveOpeansearchSource";
    public static final String DESCRIPTION = "Saves a wfs source configuration. If a pid is specified, that source configuration will be updated.";
    private OpensearchSourceConfigurationField config;

    public SaveOpensearchConfiguration() {
        super(FIELD_NAME, DESCRIPTION, new SourceInfoField());
        config = new OpensearchSourceConfigurationField();
    }

    @Override
    public SourceInfoField process(Map args) {
        return SAMPLE_OPENSEARCH_SOURCE_INFO;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
