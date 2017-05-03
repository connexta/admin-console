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
package org.codice.ddf.admin.sources.services;

import static org.codice.ddf.admin.common.services.ServiceCommons.FACTORY_PID_KEY;
import static org.codice.ddf.admin.common.services.ServiceCommons.SERVICE_PID_KEY;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

public class OpenSearchServiceProperties {

    public static final String OPENSEARCH_FACTORY_PID = "OpenSearchSource";

    public static final String ENDPOINT_URL = "endpointUrl";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String ID = "id";

    public static final String SOURCE_HOSTNAME = "sourceHostName";

    public static final String PORT = "sourcePort";

    public static final List<String> OPENSEARCH_FACTORY_PIDS = Collections.singletonList(
            OPENSEARCH_FACTORY_PID);

    public static final Function<Map<String, Object>, SourceConfigUnionField>
            SERVICE_PROPS_TO_OPENSEARCH_CONFIG =
            OpenSearchServiceProperties::servicePropsToOpenSearchConfig;

    public static final OpenSearchSourceConfigurationField servicePropsToOpenSearchConfig(
            Map<String, Object> props) {
        OpenSearchSourceConfigurationField config = new OpenSearchSourceConfigurationField();
        config.factoryPid(mapStringValue(props, FACTORY_PID_KEY));
        config.servicePid(mapStringValue(props, SERVICE_PID_KEY));
        config.sourceName(mapStringValue(props, ID));
        config.address().hostname(mapStringValue(props, SOURCE_HOSTNAME));
        config.address().port(props.get(PORT) == null ? 0 : (int) props.get(PORT));
        config.endpointUrl(mapStringValue(props, ENDPOINT_URL));
        config.credentials().username(mapStringValue(props, USERNAME));
        config.credentials().password(mapStringValue(props, PASSWORD));
        return config;
    }

    public static final Map<String, Object> openSearchConfigToServiceProps(
            OpenSearchSourceConfigurationField config) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ID, config.sourceName());
        props.put(ENDPOINT_URL, config.endpointUrl());
        if (config.credentials()
                .username() != null) {
            props.put(USERNAME,
                    config.credentials()
                            .username());
        }
        if (config.credentials()
                .password() != null) {
            props.put(PASSWORD,
                    config.credentials()
                            .password());
        }
        return props;
    }

    private static String mapStringValue(Map<String, Object> props, String key) {
        return props.get(key) == null ? null : (String) props.get(key);
    }
}
