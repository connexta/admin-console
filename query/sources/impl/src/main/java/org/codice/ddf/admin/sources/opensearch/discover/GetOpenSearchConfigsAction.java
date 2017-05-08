/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.sources.opensearch.discover;

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.getSourceConfigurations;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.SERVICE_PROPS_TO_OPENSEARCH_CONFIG;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.SourceInfoField;

import com.google.common.collect.ImmutableList;

public class GetOpenSearchConfigsAction extends BaseAction<ListFieldImpl<SourceInfoField>> {

    public static final String ID = "openSearchConfigs";

    public static final String DESCRIPTION =
            "Retrieves all currently configured OpenSearch sources. If a source pid is specified, only that source configuration will be returned.";

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    public GetOpenSearchConfigsAction(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new ListFieldImpl<>(SourceInfoField.class));
        this.configuratorFactory = configuratorFactory;
        pid = new PidField();
    }

    @Override
    public ListFieldImpl<SourceInfoField> performAction() {
        return getSourceConfigurations(OPENSEARCH_FACTORY_PIDS,
                SERVICE_PROPS_TO_OPENSEARCH_CONFIG, pid, configuratorFactory);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(pid);
    }
}
