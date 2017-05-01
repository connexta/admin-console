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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class WfsServiceProperties {
    public static final String WFS_URL = "wfsUrl";

    public static final String WFS1_FACTORY_PID = "Wfs_v1_0_0_Federated_Source";

    public static final String WFS2_FACTORY_PID = "Wfs_v2_0_0_Federated_Source";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String ID = "id";

    public static final String SOURCE_HOSTNAME = "sourceHostName";

    public static final String PORT = "sourcePort";

    public static final List<String> WFS_FACTORY_PIDS = ImmutableList.of(WFS1_FACTORY_PID,
            WFS2_FACTORY_PID);

    public static final Function<Map<String, Object>, SourceConfigUnionField>
            SERVICE_PROPS_TO_WFS_CONFIG = WfsServiceProperties::servicePropsToWfsConfig;

    public static final WfsSourceConfigurationField servicePropsToWfsConfig(
            Map<String, Object> props) {
        WfsSourceConfigurationField wfsConfig = new WfsSourceConfigurationField();
        wfsConfig.factoryPid(
                props.get(FACTORY_PID_KEY) == null ? null : (String) props.get(FACTORY_PID_KEY));
        wfsConfig.servicePid(
                props.get(SERVICE_PID_KEY) == null ? null : (String) props.get(SERVICE_PID_KEY));
        wfsConfig.sourceName(props.get(ID) == null ? null : (String) props.get(ID));
        wfsConfig.address()
                .hostname(props.get(SOURCE_HOSTNAME) == null ?
                        null :
                        (String) props.get(SOURCE_HOSTNAME));
        wfsConfig.address()
                .port(props.get(PORT) == null ? 0 : (int) props.get(PORT));
        wfsConfig.endpointUrl(props.get(WFS_URL) == null ? null : (String) props.get(WFS_URL));
        wfsConfig.credentials()
                .username(props.get(USERNAME) == null ? null : (String) props.get(USERNAME));
        wfsConfig.credentials()
                .password(props.get(PASSWORD) == null ? null : (String) props.get(PASSWORD));
        return wfsConfig;
    }

    public static final Map<String, Object> wfsConfigToServiceProps(
            WfsSourceConfigurationField configuration) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ID, configuration.sourceName());
        props.put(WFS_URL, configuration.endpointUrl());
        if (configuration.credentials()
                .username() != null) {
            props.put(USERNAME,
                    configuration.credentials()
                            .username());
        }
        if (configuration.credentials()
                .password() != null) {
            props.put(PASSWORD,
                    configuration.credentials()
                            .password());
        }
        return props;
    }
}
