package org.codice.ddf.admin.query.sources.delegate.actions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.commons.fields.common.HostnameField;
import org.codice.ddf.admin.query.commons.fields.common.PortField;
import org.codice.ddf.admin.query.commons.fields.common.UriField;
import org.codice.ddf.admin.query.sources.common.SourceConfigurationListField;
import org.codice.ddf.admin.query.sources.delegate.fields.CswSourceConfigurationField;
import org.codice.ddf.admin.query.sources.delegate.fields.WfsSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class DiscoverSourcesAction extends BaseActionField<SourceConfigurationListField> {

    public static final String FIELD_NAME = "discover";
    public static final String DESCRIPTION = "Attempts to discover sources given a hostname and port or a URL. If a URL is specified this will take precedence over host and port.";

    private HostnameField hostname = new HostnameField();
    private PortField port = new PortField();
    private UriField endpointUrl = new UriField();
    private List<Field> arguments = ImmutableList.of(hostname, port, endpointUrl);

    public DiscoverSourcesAction() {
        super(FIELD_NAME, DESCRIPTION, new SourceConfigurationListField());
    }

    @Override
    public SourceConfigurationListField process(Map args) {
        SourceConfigurationListField configs = new SourceConfigurationListField();
        configs.addField(new CswSourceConfigurationField());
        configs.addField(new WfsSourceConfigurationField());
        return configs;
//        return new CswSourceConfigurationField();
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
