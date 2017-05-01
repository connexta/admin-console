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
            Map<String, Object> cswSourceProps) {
        CswSourceConfigurationField cswConfig = new CswSourceConfigurationField();
        cswConfig.factoryPid(cswSourceProps.get(FACTORY_PID_KEY) == null ?
                null :
                (String) cswSourceProps.get(FACTORY_PID_KEY));
        cswConfig.servicePid(cswSourceProps.get(SERVICE_PID_KEY) == null ?
                null :
                (String) cswSourceProps.get(SERVICE_PID_KEY));
        cswConfig.sourceName(
                cswSourceProps.get(ID) == null ? null : (String) cswSourceProps.get(ID));
        cswConfig.address()
                .hostname((cswSourceProps.get(SOURCE_HOSTNAME) == null ?
                        null :
                        (String) cswSourceProps.get(SOURCE_HOSTNAME)));
        cswConfig.address()
                .port(cswSourceProps.get(PORT) == null ? 0 : (int) cswSourceProps.get(PORT));
        cswConfig.endpointUrl(
                cswSourceProps.get(CSW_URL) == null ? null : (String) cswSourceProps.get(CSW_URL));
        cswConfig.credentials()
                .username(cswSourceProps.get(USERNAME) == null ?
                        null :
                        (String) cswSourceProps.get(USERNAME));
        cswConfig.credentials()
                .password(cswSourceProps.get(PASSWORD) == null ?
                        null :
                        (String) cswSourceProps.get(PASSWORD));
        cswConfig.outputSchema(cswSourceProps.get(OUTPUT_SCHEMA) == null ?
                null :
                (String) cswSourceProps.get(OUTPUT_SCHEMA));
        cswConfig.forceSpatialFilter(cswSourceProps.get(FORCE_SPATIAL_FILTER) == null ?
                null :
                (String) cswSourceProps.get(FORCE_SPATIAL_FILTER));
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
}
