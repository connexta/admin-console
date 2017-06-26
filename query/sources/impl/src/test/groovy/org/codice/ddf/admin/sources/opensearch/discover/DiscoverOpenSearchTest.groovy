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
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverOpenSearchTest extends Specification {

    @Shared
            osResponseBody = this.getClass().getClassLoader().getResource('responses/opensearch/openSearchQueryResponse.xml').text

    @Shared
            badResponseBody = this.getClass().getClassLoader().getResource('responses/badResponse.xml').text

    DiscoverOpenSearchSource discoverOpenSearch

    OpenSearchSourceUtils openSearchSourceUtils

    RequestUtils requestUtils

    static TEST_OPEN_SEARCH_URL = 'https://localhost:8993/services/catalog/query'

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

        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, osResponseBody, 200, TEST_OPEN_SEARCH_URL)
        config.endpointUrl() == TEST_OPEN_SEARCH_URL
    }

    def 'Successfully discover OpenSearch Configuration using hostname and port'() {
        setup:
        discoverOpenSearch.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverOpenSearch.getValue()
        def config = (OpenSearchSourceConfigurationField) report.result()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, osResponseBody, 200, TEST_OPEN_SEARCH_URL)
        config.endpointUrl() == TEST_OPEN_SEARCH_URL
    }

    def 'Failure to discover OpenSearch configuration due to unrecognized response when using URL'() {
        setup:
        discoverOpenSearch.setValue(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def report = discoverOpenSearch.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, badResponseBody, 200, TEST_OPEN_SEARCH_URL)
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
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, osResponseBody, 500, TEST_OPEN_SEARCH_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverOpenSearchSource.FIELD_NAME]
    }

    @Ignore
    // TODO: 6/22/17 phuffer - Fix this test and the way we are mocking out RequestUtils
    def 'Cannot connect if errors from discover url from host'() {
        setup:
        discoverOpenSearch.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverOpenSearch.getValue()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(true, "", 0, "")
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages()[0].getPath() == [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()
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
}
