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
import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.common.services.ServiceCommons.SERVICE_PID_KEY;
import static org.codice.ddf.admin.common.services.ServiceCommons.mapValue;
import static org.codice.ddf.admin.sources.fields.CswProfile.CswFederatedSource.CSW_SPEC_PROFILE_FEDERATED_SOURCE;
import static org.codice.ddf.admin.sources.fields.CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE;
import static org.codice.ddf.admin.sources.fields.CswProfile.GmdCswFederatedSource.GMD_CSW_ISO_FEDERATED_SOURCE;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;

public class CswServiceProperties {

    // --- CSW Service Properties
    public static final String CSW_URL = "cswUrl";

    public static final String EVENT_SERVICE_ADDRESS = "eventServiceAddress";

    public static final String OUTPUT_SCHEMA = "outputSchema";

    public static final String FORCE_SPATIAL_FILTER = "forceSpatialFilter";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String ID = "id";
    // ---

    public static final String CSW_PROFILE_FACTORY_PID = "Csw_Federation_Profile_Source";

    public static final String CSW_GMD_FACTORY_PID = "Gmd_Csw_Federated_Source";

    public static final String CSW_SPEC_FACTORY_PID = "Csw_Federated_Source";

    public static final List<String> CSW_FACTORY_PIDS = ImmutableList.of(CSW_PROFILE_FACTORY_PID,
            CSW_GMD_FACTORY_PID,
            CSW_SPEC_FACTORY_PID);

    public static final Function<Map<String, Object>, CswSourceConfigurationField>
            SERVICE_PROPS_TO_CSW_CONFIG = CswServiceProperties::servicePropsToCswConfig;

    private static final BiMap<String, String> CSW_PROFILE_MAPPING = ImmutableBiMap.of(
            CSW_FEDERATION_PROFILE_SOURCE,
            CSW_PROFILE_FACTORY_PID,
            GMD_CSW_ISO_FEDERATED_SOURCE,
            CSW_GMD_FACTORY_PID,
            CSW_SPEC_PROFILE_FEDERATED_SOURCE,
            CSW_SPEC_FACTORY_PID);

    public static final CswSourceConfigurationField servicePropsToCswConfig(
            Map<String, Object> props) {
        CswSourceConfigurationField cswConfig = new CswSourceConfigurationField();
        cswConfig.pid(mapValue(props, SERVICE_PID_KEY));
        cswConfig.sourceName(mapValue(props, ID));
        cswConfig.endpointUrl(mapValue(props, CSW_URL));
        cswConfig.credentials()
                .username(mapValue(props, USERNAME));
        cswConfig.credentials()
                .password(FLAG_PASSWORD);
        cswConfig.outputSchema(mapValue(props, OUTPUT_SCHEMA));
        cswConfig.spatialOperator(mapValue(props, FORCE_SPATIAL_FILTER));
        cswConfig.cswProfile(factoryPidToCswProfile(mapValue(props, FACTORY_PID_KEY)));
        return cswConfig;
    }

    public static final Map<String, Object> cswConfigToServiceProps(
            CswSourceConfigurationField config) {
        ServiceCommons.ServicePropertyBuilder builder =
                new ServiceCommons.ServicePropertyBuilder().putPropertyIfNotNull(ID,
                        config.sourceNameField())
                        .putPropertyIfNotNull(CSW_URL, config.endpointUrlField())
                        .putPropertyIfNotNull(USERNAME,
                                config.credentials()
                                        .usernameField())
                        .putPropertyIfNotNull(OUTPUT_SCHEMA, config.outputSchemaField())
                        .putPropertyIfNotNull(FORCE_SPATIAL_FILTER, config.spatialOperatorField());

        String password = config.credentials()
                .password();
        if (password != null && !password.equals(FLAG_PASSWORD)) {
            builder.put(PASSWORD,
                    config.credentials()
                            .password());
        }

        if (config.endpointUrl() != null && config.cswProfile()
                .equals(CSW_FEDERATION_PROFILE_SOURCE)) {
            builder.put(EVENT_SERVICE_ADDRESS, config.endpointUrl() + "/subscription");
        }
        return builder.build();
    }

    public static String cswProfileToFactoryPid(String cswProfile) {
        return CSW_PROFILE_MAPPING.get(cswProfile);
    }

    public static String factoryPidToCswProfile(String factoryPid) {
        return CSW_PROFILE_MAPPING.inverse()
                .get(factoryPid);
    }
}
