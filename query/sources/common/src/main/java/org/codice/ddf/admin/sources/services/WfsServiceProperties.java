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

import org.codice.ddf.admin.sources.fields.WfsVersion;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;

public class WfsServiceProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(WfsServiceProperties.class);

    public static final String WFS_URL = "wfsUrl";

    public static final String WFS1_FACTORY_PID = "Wfs_v1_0_0_Federated_Source";

    public static final String WFS2_FACTORY_PID = "Wfs_v2_0_0_Federated_Source";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String ID = "id";

    public static final List<String> WFS_FACTORY_PIDS = ImmutableList.of(WFS1_FACTORY_PID,
            WFS2_FACTORY_PID);

    public static final Function<Map<String, Object>, SourceConfigUnionField>
            SERVICE_PROPS_TO_WFS_CONFIG = WfsServiceProperties::servicePropsToWfsConfig;

    private static final BiMap<String, String> WFS_VERSION_MAPPING = ImmutableBiMap.of(
            WfsVersion.WFS_VERSION_1, WFS1_FACTORY_PID,
            WfsVersion.WFS_VERSION_2, WFS2_FACTORY_PID);

    public static WfsSourceConfigurationField servicePropsToWfsConfig(
            Map<String, Object> props) {
        WfsSourceConfigurationField wfsConfig = new WfsSourceConfigurationField();
        wfsConfig.pid(mapStringValue(props, SERVICE_PID_KEY));
        wfsConfig.sourceName(mapStringValue(props, ID));
        wfsConfig.endpointUrl(mapStringValue(props, WFS_URL));
        wfsConfig.credentials().username(mapStringValue(props, USERNAME));
        wfsConfig.credentials().password(mapStringValue(props, PASSWORD));
        try {
            wfsConfig.wfsVersion(wfsFactoryPidToVersion(mapStringValue(props, FACTORY_PID_KEY)));
        } catch (IllegalArgumentException ignored) {
        }
        return wfsConfig;
    }

    public static Map<String, Object> wfsConfigToServiceProps(
            WfsSourceConfigurationField configuration) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ID, configuration.sourceName());
        props.put(WFS_URL, configuration.endpointUrl());
        if (configuration.credentials().username() != null) {
            props.put(USERNAME, configuration.credentials().username());
        }
        if (configuration.credentials().password() != null) {
            props.put(PASSWORD, configuration.credentials().password());
        }
        return props;
    }

    public static String wfsVersionToFactoryPid(String wfsVersion) throws IllegalArgumentException {
        String factoryPid = WFS_VERSION_MAPPING.get(wfsVersion);
        if(factoryPid == null) {
            LOGGER.debug("Received invalid wfsVersion [{}]. Valid values are [{}]", wfsVersion, WFS_VERSION_MAPPING.values());
            throw new IllegalArgumentException(String.format("Invalid WFS version [%s].", wfsVersion));
        }
        return factoryPid;
    }

    public static String wfsFactoryPidToVersion(String factoryPid) throws IllegalArgumentException {
        String wfsVersion = WFS_VERSION_MAPPING.inverse().get(factoryPid);
        if(wfsVersion == null) {
            LOGGER.debug("Received invalid factoryPid [{}]. Valid values are [{}]", factoryPid, WFS_VERSION_MAPPING.keySet());
            throw new IllegalArgumentException(String.format("Invalid WFS version [%s].", factoryPid));
        }
        return wfsVersion;
    }

    private static String mapStringValue(Map<String, Object> props, String key) {
        return props.get(key) == null ? null : (String) props.get(key);
    }
}
