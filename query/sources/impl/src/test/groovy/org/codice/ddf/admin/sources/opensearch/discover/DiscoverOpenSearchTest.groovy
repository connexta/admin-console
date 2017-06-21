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
package org.codice.ddf.admin.sources.opensearch.discover

import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.fields.common.ResponseField
import org.codice.ddf.admin.common.report.ReportWithResultImpl
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverOpenSearchTest extends Specification {

    DiscoverOpenSearchSource discoverOpenSearch

    OpenSearchSourceUtils openSearchSourceUtils

    RequestUtils requestUtils

    static TEST_OPEN_SEARCH_URL = 'https://localhost:8993/services/catalog/query'

    static OPEN_SEARCH_CAPABILITIES_FILE_PATH = 'responses/opensearch/openSearchQueryResponse.xml'

    static BASE_PATH = [DiscoverOpenSearchSource.FIELD_NAME, FunctionField.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def setup() {
        requestUtils = Mock(RequestUtils)
        openSearchSourceUtils = new OpenSearchSourceUtils(requestUtils, new SourceUtilCommons())
        discoverOpenSearch = new DiscoverOpenSearchSource(openSearchSourceUtils)
    }

    def 'Successfully discover OpenSearch configuration using URL'() {
        setup:
        discoverOpenSearch.setValue(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def report = discoverOpenSearch.getValue()
        def config = (OpenSearchSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, OPEN_SEARCH_CAPABILITIES_FILE_PATH, 200, TEST_OPEN_SEARCH_URL)
        config.endpointUrl() == TEST_OPEN_SEARCH_URL
    }

    def 'Successfully discover OpenSearch Configuration using hostname and port'() {
        setup:
        discoverOpenSearch.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverOpenSearch.getValue()
        def config = (OpenSearchSourceConfigurationField) report.result()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, OPEN_SEARCH_CAPABILITIES_FILE_PATH, 200, TEST_OPEN_SEARCH_URL)
        config.endpointUrl() == TEST_OPEN_SEARCH_URL
    }

    def 'Failure to discover OpenSearch configuration due to unrecognized response when using URL'() {
        setup:
        discoverOpenSearch.setValue(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def report = discoverOpenSearch.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, 'responses/badResponse.xml', 200, TEST_OPEN_SEARCH_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverOpenSearchSource.FIELD_NAME]
    }

    def 'Unknown endpoint with bad HTTP status code received'() {
        setup:
        discoverOpenSearch.setValue(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def report = discoverOpenSearch.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, OPEN_SEARCH_CAPABILITIES_FILE_PATH, 500, TEST_OPEN_SEARCH_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverOpenSearchSource.FIELD_NAME]
    }

    def 'Fail when missing required fields'() {
        when:
        def report = discoverOpenSearch.getValue()

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
