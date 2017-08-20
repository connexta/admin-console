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

import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import spock.lang.Shared

class DiscoverOpenSearchSpec extends SourceCommonsSpec {

    @Shared
            osResponseBody = this.getClass().getClassLoader().getResource('responses/opensearch/openSearchQueryResponse.xml').text

    @Shared
            badResponseBody = this.getClass().getClassLoader().getResource('responses/badResponse.xml').text

    DiscoverOpenSearchSource discoverOpenSearch

    static TEST_OPEN_SEARCH_URL = 'https://testHostName:12345/services/catalog/query'

    static BASE_PATH = [DiscoverOpenSearchSource.FIELD_NAME, FunctionField.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOST_FIELD_PATH = [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def setup() {
        discoverOpenSearch = new DiscoverOpenSearchSource(Mock(ConfiguratorSuite))
    }

    def 'Successfully discover OpenSearch configuration using URL'() {
        setup:
        discoverOpenSearch.setOpenSearchSourceUtils(prepareOpenSearchSourceUtils(200, osResponseBody, true))
        discoverOpenSearch.setArguments(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def report = discoverOpenSearch.execute()
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_OPEN_SEARCH_URL
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover OpenSearch Configuration using hostname and port'() {
        setup:
        discoverOpenSearch.setOpenSearchSourceUtils(prepareOpenSearchSourceUtils(200, osResponseBody, true))
        discoverOpenSearch.setArguments(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverOpenSearch.execute()
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Failure to discover OpenSearch configuration due to unrecognized response when using URL'() {
        setup:
        discoverOpenSearch.setOpenSearchSourceUtils(prepareOpenSearchSourceUtils(200, badResponseBody, true))
        discoverOpenSearch.setArguments(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def report = discoverOpenSearch.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint with bad HTTP status code received'() {
        setup:
        discoverOpenSearch.setOpenSearchSourceUtils(prepareOpenSearchSourceUtils(500, osResponseBody, true))
        discoverOpenSearch.setArguments(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def report = discoverOpenSearch.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint if no pre-formatted URLs work when discovering with host+port'() {
        setup:
        discoverOpenSearch.setOpenSearchSourceUtils(prepareOpenSearchSourceUtils(200, osResponseBody, false))
        discoverOpenSearch.setArguments(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverOpenSearch.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == HOST_FIELD_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = discoverOpenSearch.execute()

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.getErrorMessages()*.getPath() == [URL_FIELD_PATH]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        DiscoverOpenSearchSource cannotConnectOpenSearch = new DiscoverOpenSearchSource(Mock(ConfiguratorSuite))
        cannotConnectOpenSearch.setOpenSearchSourceUtils(prepareOpenSearchSourceUtils(200, osResponseBody, false))
        cannotConnectOpenSearch.setArguments(getBaseDiscoverByAddressArgs())

        DiscoverOpenSearchSource unknownEndpointOpenSearch = new DiscoverOpenSearchSource(Mock(ConfiguratorSuite))
        unknownEndpointOpenSearch.setOpenSearchSourceUtils(prepareOpenSearchSourceUtils(200, badResponseBody, true))
        unknownEndpointOpenSearch.setArguments(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL))

        when:
        def errorCodes = discoverOpenSearch.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectOpenSearch.execute()
        def unknownEndpointReport = unknownEndpointOpenSearch.execute()

        then:
        errorCodes.size() == 2
        errorCodes.contains(cannotConnectReport.getErrorMessages()[0].getCode())
        errorCodes.contains(unknownEndpointReport.getErrorMessages()[0].getCode())
    }

    def prepareOpenSearchSourceUtils(int statusCode, String responseBody, boolean endpointIsReachable) {
        def requestUtils = new TestRequestUtils(createMockWebClientBuilder(statusCode, responseBody), endpointIsReachable)
        def openSearchUtils = new OpenSearchSourceUtils(Mock(ConfiguratorSuite))
        openSearchUtils.setRequestUtils(requestUtils)
        return openSearchUtils
    }
}
