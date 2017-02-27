package org.codice.ddf.admin.query.sources.delegate.actions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.DefaultAction;
import org.codice.ddf.admin.query.commons.fields.common.HostnameField;
import org.codice.ddf.admin.query.commons.fields.common.PortField;
import org.codice.ddf.admin.query.commons.fields.common.UriField;
import org.codice.ddf.admin.query.sources.common.SourceConfigUnionField;
import org.codice.ddf.admin.query.sources.common.SourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.SourceConfigurationListField;
import org.codice.ddf.admin.query.sources.delegate.fields.CswSourceConfigurationField;
import org.codice.ddf.admin.query.sources.delegate.fields.WfsSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class DiscoverSourcesAction extends DefaultAction<SourceConfigurationListField> {

    public static final String ACTION_ID = "discover";
    public static final String DESCRIPTION = "Attempts to discover sources given a hostname and port or a URL. If a URL is specified this will take precedence over host and port.";
    public static final List<Field> OPTIONAL_FIELDS = ImmutableList.of(new HostnameField(), new PortField(), new UriField());

    public DiscoverSourcesAction() {
        super(ACTION_ID, DESCRIPTION, null, OPTIONAL_FIELDS, new SourceConfigurationListField());
    }

    @Override
    public SourceConfigurationListField process(Map args) {
        SourceConfigurationListField configs = new SourceConfigurationListField();
        configs.addField(new CswSourceConfigurationField());
        configs.addField(new WfsSourceConfigurationField());
        return configs;
//        return new CswSourceConfigurationField();
    }
}
