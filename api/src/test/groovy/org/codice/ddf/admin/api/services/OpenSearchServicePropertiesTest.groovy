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

import org.codice.ddf.admin.api.config.sources.OpenSearchSourceConfiguration
import spock.lang.Specification

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.ENDPOINT_URL
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME
import static org.codice.ddf.admin.api.services.OpenSearchServiceProperties.*
import static org.codice.ddf.admin.api.validation.ValidationUtils.FACTORY_PID_KEY
import static org.codice.ddf.admin.api.validation.ValidationUtils.SERVICE_PID_KEY

class OpenSearchServicePropertiesTest extends Specification {

    // General Configuration Properties
    private static final TEST_FACTORY_PID = "testFactoryPid"
    private static final TEST_SERVICE_PID = "testServicePid"
    // Source Configuration Properties
    private static final TEST_ID = "testId"
    private static final TEST_HOSTNAME = "testHostname"
    private static final TEST_PORT = 443
    private static final TEST_URL = "testUrl"
    private static final TEST_USERNAME = "testUsername"
    private static final TEST_PASSWORD = "testPassword"

    def 'test populated map to OpenSearch config'() {
        setup:
        def props = [FACTORY_PID_KEY, TEST_FACTORY_PID,
                     SERVICE_PID_KEY, TEST_SERVICE_PID,
                     ID, TEST_ID,
                     SOURCE_HOSTNAME, TEST_HOSTNAME,
                     PORT, TEST_PORT,
                     ENDPOINT_URL, TEST_URL,
                     USERNAME, TEST_USERNAME,
                     PASSWORD, TEST_PASSWORD]
                .toSpreadMap()

        when:
        def config = servicePropsToOpenSearchConfig(props)

        then:
        config.factoryPid() == TEST_FACTORY_PID
        config.servicePid() == TEST_SERVICE_PID
        config.sourceName() == TEST_ID
        config.sourceHostName() == TEST_HOSTNAME
        config.sourcePort().equals(TEST_PORT)
        config.endpointUrl() == TEST_URL
        config.sourceUserName() == TEST_USERNAME
        config.sourceUserPassword() == TEST_PASSWORD
    }

    def 'test empty map to OpenSearch config'() {
        setup:
        def props = new HashMap()

        when:
        def config = servicePropsToOpenSearchConfig(props)

        then:
        config.factoryPid() == null
        config.servicePid() == null
        config.sourceName() == null
        config.sourceHostName() == null
        config.sourcePort().equals(0)
        config.sourceUserName() == null
        config.sourceUserPassword() == null
        config.endpointUrl() == null
    }

    def 'test populated OpenSearch config to map'() {
        setup:
        def config = Mock(OpenSearchSourceConfiguration)

        when:
        def props = openSearchConfigToServiceProps(config)

        then:
        1 * config.sourceName() >> TEST_ID
        1 * config.endpointUrl() >> TEST_URL
        2 * config.sourceUserName() >> TEST_USERNAME
        2 * config.sourceUserPassword() >> TEST_PASSWORD
        props.get(ID) == TEST_ID
        props.get(ENDPOINT_URL) == TEST_URL
        props.get(USERNAME) == TEST_USERNAME
        props.get(PASSWORD) == TEST_PASSWORD
    }

    def 'test empty OpenSearch config to map'() {
        setup:
        def config = Mock(OpenSearchSourceConfiguration)

        when:
        def props = openSearchConfigToServiceProps(config)

        then:
        1 * config.sourceName() >> null
        1 * config.endpointUrl() >> null
        1 * config.sourceUserName() >> null
        1 * config.sourceUserPassword() >> null
        props.get(ID) == null
        props.get(ENDPOINT_URL) == null
        props.get(USERNAME) == null
        props.get(PASSWORD) == null
    }
}