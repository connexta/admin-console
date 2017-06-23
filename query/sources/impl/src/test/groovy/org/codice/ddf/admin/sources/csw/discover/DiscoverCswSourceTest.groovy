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
package org.codice.ddf.admin.sources.csw.discover

import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.sources.csw.CswSourceUtils
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverCswSourceTest extends Specification {

    @Shared
            ddfCswResponse = this.getClass().getClassLoader().getResource('responses/csw/DDFCswGetCapabilities.xml').text

    @Shared
            specCswResponse = this.getClass().getClassLoader().getResource('responses/csw/specCswGetCapabilities.xml').text

    @Shared
            gmdCswResponse = this.getClass().getClassLoader().getResource('responses/csw/gmdCswGetCapabilities.xml').text

    @Shared
            unrecognizedCswResponse = this.getClass().getClassLoader().getResource('responses/csw/unrecognizedCswSchema.xml').text

    @Shared
            noOutputSchemaCswResponse = this.getClass().getClassLoader().getResource('responses/csw/noOutputSchemaResponse.xml').text

    @Shared
            badResponseBody = this.getClass().getClassLoader().getResource('responses/badResponse.xml').text

    static TEST_CSW_URL = 'https://localhost:8993/services/csw'

    static NO_FILTER = 'NO_FILTER'

    DiscoverCswSource discoverCsw

    CswSourceUtils cswSourceUtils

    RequestUtils requestUtils

    static BASE_PATH = [DiscoverCswSource.FIELD_NAME, FunctionField.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def setup() {
        requestUtils = Mock(RequestUtils)
        cswSourceUtils = new CswSourceUtils(requestUtils, new SourceUtilCommons())
        discoverCsw = new DiscoverCswSource(cswSourceUtils)
    }

    def 'Successfully discover DDF federation profile with URL'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, ddfCswResponse, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE
        config.outputSchema() == CswSourceUtils.METACARD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }

    def 'Successfully discover CSWSpecification federation profile with URL'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, specCswResponse, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CswFederatedSource.CSW_SPEC_PROFILE_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.CSW_2_0_2_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }

    def 'Successfully discover GMD CSW federation profile with URL'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, gmdCswResponse, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.GmdCswFederatedSource.GMD_CSW_ISO_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.GMD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }

    def 'Successfully discover DDF federation profile using hostname and port'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, ddfCswResponse, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE
        config.outputSchema() == CswSourceUtils.METACARD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }


    def 'Successfully discover CSWSpecification federation profile using hostname and port'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, specCswResponse, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CswFederatedSource.CSW_SPEC_PROFILE_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.CSW_2_0_2_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }

    def 'Successfully discover GMD CSW federation profile using hostname and port'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, gmdCswResponse, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.GmdCswFederatedSource.GMD_CSW_ISO_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.GMD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }

    def 'Unknown endpoint error with bad HTTP status'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, gmdCswResponse, 500, TEST_CSW_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverCswSource.FIELD_NAME]
    }

    def 'Unknown endpoint error with unrecognized response when using URL'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, badResponseBody, 200, TEST_CSW_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverCswSource.FIELD_NAME]
    }

    def 'Unknown endpoint error with unrecognized response when using hostname+port'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverCsw.getValue()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, badResponseBody, 200, TEST_CSW_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverCswSource.FIELD_NAME]
    }

    @Ignore
    // TODO: 6/22/17 phuffer - Fix this test and the way we are mocking out RequestUtils
    def 'Cannot connect if errors from discover url from host'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverCsw.getValue()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(true, "", 0, "")
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages()[0].getPath() == [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()
    }

    def 'Unrecognized CSW output schema defaults to CSW 2.0.2 schema'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, unrecognizedCswResponse, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CswFederatedSource.CSW_SPEC_PROFILE_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.CSW_2_0_2_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }

    def 'Unknown endpoint error when there is no output schema'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, noOutputSchemaCswResponse, 200, TEST_CSW_URL)
        report.result() == null
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverCswSource.FIELD_NAME]
    }

    def 'Fail when missing required fields'() {
        when:
        def report = discoverCsw.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [URL_FIELD_PATH]
    }
}
