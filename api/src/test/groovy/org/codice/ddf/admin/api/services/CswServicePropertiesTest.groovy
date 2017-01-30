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
 **/
package org.codice.ddf.admin.api.services

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration
import spock.lang.Specification

import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_URL
import static org.codice.ddf.admin.api.services.CswServiceProperties.EVENT_SERVICE_ADDRESS
import static org.codice.ddf.admin.api.services.CswServiceProperties.FORCE_SPATIAL_FILTER
import static org.codice.ddf.admin.api.services.CswServiceProperties.ID
import static org.codice.ddf.admin.api.services.CswServiceProperties.OUTPUT_SCHEMA
import static org.codice.ddf.admin.api.validation.ValidationUtils.FACTORY_PID_KEY
import static org.codice.ddf.admin.api.validation.ValidationUtils.SERVICE_PID_KEY
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT
import static org.codice.ddf.admin.api.services.CswServiceProperties.PASSWORD
import static org.codice.ddf.admin.api.services.CswServiceProperties.USERNAME

class CswServicePropertiesTest extends Specification {

    // General Configuration Properties
    private static final TEST_FACTORY_PID = "testFactoryPid"
    private static final TEST_SERVICE_PID = "testServicePid"
    // Source Configuration Properties
    private static final TEST_ID = "testId"
    private static final TEST_HOSTNAME = "testHostname"
    private static final TEST_PORT = 443
    private static final TEST_CSW_URL = "testCswUrl"
    private static final TEST_USERNAME = "testUsername"
    private static final TEST_PASSWORD = "testPassword"
    // CSW Source Configuration Properties
    private static final TEST_OUTPUT_SCHEMA = "testOutputSchema"
    private static final TEST_FORCE_SPATIAL_FILTER = "testForceSpatialFilter"

    def 'test populated map to CSW config' () {
        setup:
        def props = [FACTORY_PID_KEY, TEST_FACTORY_PID,
                     SERVICE_PID_KEY, TEST_SERVICE_PID,
                     ID, TEST_ID,
                     SOURCE_HOSTNAME, TEST_HOSTNAME,
                     PORT, TEST_PORT,
                     CSW_URL, TEST_CSW_URL,
                     USERNAME, TEST_USERNAME,
                     PASSWORD, TEST_PASSWORD,
                     OUTPUT_SCHEMA, TEST_OUTPUT_SCHEMA,
                     FORCE_SPATIAL_FILTER, TEST_FORCE_SPATIAL_FILTER]
                .toSpreadMap()

        when:
        def config = CswServiceProperties.servicePropsToCswConfig(props)

        then:
        config.factoryPid() == TEST_FACTORY_PID
        config.servicePid() == TEST_SERVICE_PID
        config.sourceName() == TEST_ID
        config.sourceHostName() == TEST_HOSTNAME
        config.sourcePort().equals(TEST_PORT)
        config.endpointUrl() == TEST_CSW_URL
        config.sourceUserName() == TEST_USERNAME
        config.sourceUserPassword() == TEST_PASSWORD
        config.outputSchema() == TEST_OUTPUT_SCHEMA
        config.forceSpatialFilter() == TEST_FORCE_SPATIAL_FILTER
    }

    def 'test empty map to CSW config'() {

        setup:
        def props = new HashMap()

        when:
        def config = CswServiceProperties.servicePropsToCswConfig(props)

        then:
        config.factoryPid() == null
        config.servicePid() == null
        config.sourceName() == null
        config.sourceHostName() == null
        config.sourcePort().equals(0)
        config.sourceUserName() == null
        config.sourceUserPassword() == null
        config.endpointUrl() == null
        config.outputSchema() == null
        config.forceSpatialFilter() == null
    }

    def 'test empty CSW config to map'() {
        setup:
        def config = Mock(CswSourceConfiguration)

        when:
        def props = CswServiceProperties.cswConfigToServiceProps(config)

        then:
        1 * config.sourceName() >> null
        1 * config.endpointUrl() >> null
        1 * config.factoryPid() >> null
        1 * config.sourceUserName() >> null
        1 * config.sourceUserPassword() >> null
        1 * config.outputSchema() >> null
        1 * config.forceSpatialFilter() >> null
        props.get(ID) == null
        props.get(CSW_URL) == null
        props.get(EVENT_SERVICE_ADDRESS) == null
        props.get(USERNAME) == null
        props.get(PASSWORD) == null
        props.get(OUTPUT_SCHEMA) == null
        props.get(FORCE_SPATIAL_FILTER) == null
    }

    def 'test populated CSW config to map'() {
        setup:
        def config = Mock(CswSourceConfiguration)

        when:
        def props = CswServiceProperties.cswConfigToServiceProps(config)

        then:
        1 * config.sourceName() >> "test"
        2 * config.endpointUrl() >> TEST_CSW_URL
        2 * config.factoryPid() >> TEST_FACTORY_PID
        2 * config.sourceUserName() >> TEST_USERNAME
        2 * config.sourceUserPassword() >> TEST_PASSWORD
        2 * config.outputSchema() >> TEST_OUTPUT_SCHEMA
        2 * config.forceSpatialFilter() >> TEST_FORCE_SPATIAL_FILTER
        props.get(ID) == "test"
        props.get(CSW_URL) == TEST_CSW_URL
        props.get(EVENT_SERVICE_ADDRESS) == TEST_CSW_URL + "/subscription"
        props.get(USERNAME) == TEST_USERNAME
        props.get(PASSWORD) == TEST_PASSWORD
        props.get(OUTPUT_SCHEMA) == TEST_OUTPUT_SCHEMA
        props.get(FORCE_SPATIAL_FILTER) == TEST_FORCE_SPATIAL_FILTER
    }

}