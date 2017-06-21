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
import org.codice.ddf.admin.common.fields.common.ResponseField
import org.codice.ddf.admin.common.report.ReportWithResultImpl
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import org.codice.ddf.admin.sources.csw.CswSourceUtils
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverCswSourceTest extends Specification {

    static DDF_CSW_GET_CAPABILTIES_FILE_PATH = 'responses/csw/DDFCswGetCapabilities.xml'

    static SPEC_CSW_GET_CAPABILITIES_FILE_PATH = 'responses/csw/specCswGetCapabilities.xml'

    static GMD_CSW_GET_CAPABILITIES_FILE_PATH = 'responses/csw/gmdCswGetCapabilities.xml'

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
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, DDF_CSW_GET_CAPABILTIES_FILE_PATH, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CSW_FEDERATION_PROFILE_SOURCE
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
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, SPEC_CSW_GET_CAPABILITIES_FILE_PATH, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CSW_SPEC_PROFILE_FEDERATED_SOURCE
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
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, GMD_CSW_GET_CAPABILITIES_FILE_PATH, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.GMD_CSW_ISO_FEDERATED_SOURCE
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
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, DDF_CSW_GET_CAPABILTIES_FILE_PATH, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CSW_FEDERATION_PROFILE_SOURCE
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
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, SPEC_CSW_GET_CAPABILITIES_FILE_PATH, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CSW_SPEC_PROFILE_FEDERATED_SOURCE
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
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, GMD_CSW_GET_CAPABILITIES_FILE_PATH, 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.GMD_CSW_ISO_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.GMD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
    }

    def 'Unknown endpoint error with bad HTTP status'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, GMD_CSW_GET_CAPABILITIES_FILE_PATH, 500, TEST_CSW_URL)
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
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, "responses/badResponse.xml", 200, TEST_CSW_URL)
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
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, "responses/badResponse.xml", 200, TEST_CSW_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverCswSource.FIELD_NAME]
    }

    def 'Unrecognized CSW output schema defaults to CSW 2.0.2 schema'() {
        setup:
        discoverCsw.setValue(getBaseDiscoverByUrlArgs(TEST_CSW_URL))

        when:
        def report = discoverCsw.getValue()
        def config = (CswSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, "responses/csw/unrecognizedCswSchema.xml", 200, TEST_CSW_URL)
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CSW_SPEC_PROFILE_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.CSW_2_0_2_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
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

    def createResponseFieldResult(boolean hasError, String filePath, int code, String requestUrl) {
        def result = new ReportWithResultImpl<ResponseField>()
        if (hasError) {
            result.addArgumentMessage(new ErrorMessageImpl("code", []))
        } else {
            ResponseField responseField = new ResponseField()
            responseField.responseBody(this.getClass().getClassLoader().getResource(filePath).text)
            responseField.statusCode(code)
            responseField.requestUrl(requestUrl)
            result.result(responseField)
        }

        return result
    }
}
