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
package org.codice.ddf.admin.sources.commons.services;

import static org.codice.ddf.admin.sources.commons.services.SourceServiceProperties.FACTORY_PID_KEY;
import static org.codice.ddf.admin.sources.commons.services.SourceServiceProperties.ID;
import static org.codice.ddf.admin.sources.commons.services.SourceServiceProperties.PASSWORD;
import static org.codice.ddf.admin.sources.commons.services.SourceServiceProperties.PORT;
import static org.codice.ddf.admin.sources.commons.services.SourceServiceProperties.SERVICE_PID_KEY;
import static org.codice.ddf.admin.sources.commons.services.SourceServiceProperties.SOURCE_HOSTNAME;
import static org.codice.ddf.admin.sources.commons.services.SourceServiceProperties.USERNAME;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.sources.fields.type.OpensearchSourceConfigurationField;

public class OpenSearchServiceProperties {

    public static final String OPENSEARCH_FACTORY_PID = "OpenSearchSource";

    public static final String ENDPOINT_URL = "endpointUrl";

    public static final List<String> OPENSEARCH_FACTORY_PIDS = Collections.singletonList(
            OPENSEARCH_FACTORY_PID);

    public static final OpensearchSourceConfigurationField servicePropsToOpenSearchConfig(
            Map<String, Object> props) {
        OpensearchSourceConfigurationField config = new OpensearchSourceConfigurationField();
        config.factoryPid(
                props.get(FACTORY_PID_KEY) == null ? null : (String) props.get(FACTORY_PID_KEY));
        config.servicePid(
                props.get(SERVICE_PID_KEY) == null ? null : (String) props.get(SERVICE_PID_KEY));
        config.sourceName(props.get(ID) == null ? null : (String) props.get(ID));
        config.address()
                .hostname(props.get(SOURCE_HOSTNAME) == null ?
                        null :
                        (String) props.get(SOURCE_HOSTNAME));
        config.address()
                .port(props.get(PORT) == null ? 0 : (int) props.get(PORT));
        config.endpointUrl(
                props.get(ENDPOINT_URL) == null ? null : (String) props.get(ENDPOINT_URL));
        config.credentials()
                .username(props.get(USERNAME) == null ? null : (String) props.get(USERNAME));
        config.credentials()
                .password(props.get(PASSWORD) == null ? null : (String) props.get(PASSWORD));
        return config;
    }

    public static final Map<String, Object> openSearchConfigToServiceProps(
            OpensearchSourceConfigurationField config) {
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
}
