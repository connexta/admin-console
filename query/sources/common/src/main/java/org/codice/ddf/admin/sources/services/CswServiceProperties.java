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
import static org.codice.ddf.admin.sources.fields.CswProfile.CSW_FEDERATION_PROFILE_SOURCE;
import static org.codice.ddf.admin.sources.fields.CswProfile.CSW_SPEC_PROFILE_FEDERATED_SOURCE;
import static org.codice.ddf.admin.sources.fields.CswProfile.GMD_CSW_ISO_FEDERATED_SOURCE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

import com.google.common.collect.ImmutableList;

public class CswServiceProperties {

    public static final String CSW_URL = "cswUrl";

    public static final String EVENT_SERVICE_ADDRESS = "eventServiceAddress";

    public static final String OUTPUT_SCHEMA = "outputSchema";

    public static final String FORCE_SPATIAL_FILTER = "forceSpatialFilter";

    public static final String CSW_PROFILE_FACTORY_PID = "Csw_Federation_Profile_Source";

    public static final String CSW_GMD_FACTORY_PID = "Gmd_Csw_Federated_Source";

    public static final String CSW_SPEC_FACTORY_PID = "Csw_Federated_Source";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String ID = "id";

    public static final String SOURCE_HOSTNAME = "sourceHostName";

    public static final String PORT = "sourcePort";

    public static final List<String> CSW_FACTORY_PIDS = ImmutableList.of(CSW_PROFILE_FACTORY_PID,
            CSW_GMD_FACTORY_PID,
            CSW_SPEC_FACTORY_PID);

    public static final Function<Map<String, Object>, SourceConfigUnionField>
            SERVICE_PROPS_TO_CSW_CONFIG = CswServiceProperties::servicePropsToCswConfig;

    public static final CswSourceConfigurationField servicePropsToCswConfig(
            Map<String, Object> props) {
        CswSourceConfigurationField cswConfig = new CswSourceConfigurationField();
        cswConfig.factoryPid(mapStringValue(props, FACTORY_PID_KEY));
        cswConfig.servicePid(mapStringValue(props, SERVICE_PID_KEY));
        cswConfig.sourceName(mapStringValue(props, ID));
        cswConfig.address().hostname(mapStringValue(props, SOURCE_HOSTNAME));
        cswConfig.address().port(props.get(PORT) == null ? -1 : (int) props.get(PORT));
        cswConfig.endpointUrl(mapStringValue(props, CSW_URL));
        cswConfig.credentials().username(mapStringValue(props, USERNAME));
        cswConfig.credentials().password(mapStringValue(props, PASSWORD));
        cswConfig.outputSchema(mapStringValue(props, OUTPUT_SCHEMA));
        cswConfig.forceSpatialFilter(mapStringValue(props, FORCE_SPATIAL_FILTER));
        return cswConfig;
    }

    public static final Map<String, Object> cswConfigToServiceProps(
            CswSourceConfigurationField config) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ID, config.sourceName());
        props.put(CSW_URL, config.endpointUrl());
        if (config.eventServiceAddress() != null) {
            props.put(EVENT_SERVICE_ADDRESS, config.eventServiceAddress());
        } else if (config.factoryPid() != null && !config.factoryPid()
                .equals(CSW_GMD_FACTORY_PID)) {
            props.put(EVENT_SERVICE_ADDRESS, config.endpointUrl() + "/subscription");
        }

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
        if (config.outputSchema() != null) {
            props.put(OUTPUT_SCHEMA, config.outputSchema());
        }
        if (config.forceSpatialFilter() != null) {
            props.put(FORCE_SPATIAL_FILTER, config.forceSpatialFilter());
        }
        return props;
    }

    public static String resolveCswFactoryPid(String cswProfile) {
        switch(cswProfile) {
        case CSW_FEDERATION_PROFILE_SOURCE:
            return CSW_PROFILE_FACTORY_PID;
        case GMD_CSW_ISO_FEDERATED_SOURCE:
            return CSW_GMD_FACTORY_PID;
        case CSW_SPEC_PROFILE_FEDERATED_SOURCE:
            return CSW_SPEC_FACTORY_PID;
        default:
            throw new IllegalArgumentException(String.format("Invalid CSW Profile specified [%s].", cswProfile));
        }
    }

    private static String mapStringValue(Map<String, Object> props, String key) {
        return props.get(key) == null ? null : (String) props.get(key);
    }
}
