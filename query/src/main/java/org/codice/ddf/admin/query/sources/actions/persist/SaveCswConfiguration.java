package org.codice.ddf.admin.query.sources.actions.persist;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_CSW_SOURCE_INFO;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.sources.common.SourceInfoField;
import org.codice.ddf.admin.query.sources.common.fields.CswSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveCswConfiguration extends BaseAction<SourceInfoField> {

    public static final String NAME = "saveCswSource";
    public static final String DESCRIPTION = "Saves a csw source configuration. If a pid is specified, that source configuration will be updated.";
    private CswSourceConfigurationField config;

    public SaveCswConfiguration() {
        super(NAME, DESCRIPTION, new SourceInfoField());
        config = new CswSourceConfigurationField();
    }

    @Override
    public SourceInfoField process() {
        return SAMPLE_CSW_SOURCE_INFO;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
