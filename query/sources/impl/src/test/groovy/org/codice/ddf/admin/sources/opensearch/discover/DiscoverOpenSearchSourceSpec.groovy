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

import org.apache.cxf.jaxrs.client.WebClient
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.report.Reports
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import org.codice.ddf.cxf.client.ClientFactoryFactory
import org.codice.ddf.cxf.client.SecureCxfClientFactory
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import spock.lang.Shared

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class DiscoverOpenSearchSourceSpec extends SourceCommonsSpec {

    static final List<Object> FUNCTION_PATH = [DiscoverOpenSearchSource.FIELD_NAME]

    @Shared
            osResponseBody = this.getClass().getClassLoader().getResource('responses/opensearch/openSearchQueryResponse.xml').text

    @Shared
            badResponseBody = this.getClass().getClassLoader().getResource('responses/badResponse.xml').text

    DiscoverOpenSearchSource discoverOpenSearch

    static TEST_OPEN_SEARCH_URL = 'https://testHostName:12345/services/catalog/query'

    static BASE_PATH = [DiscoverOpenSearchSource.FIELD_NAME]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOST_FIELD_PATH = [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def 'Successfully discover OpenSearch configuration using URL'() {
        setup:
        discoverOpenSearch = new DiscoverOpenSearchSource(prepareOpenSearchSourceUtils(200, osResponseBody, true))

        when:
        def report = discoverOpenSearch.execute(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL), FUNCTION_PATH)
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_OPEN_SEARCH_URL
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover OpenSearch Configuration using hostname and port'() {
        setup:
        discoverOpenSearch = new DiscoverOpenSearchSource(prepareOpenSearchSourceUtils(200, osResponseBody, true))

        when:
        def report = discoverOpenSearch.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Failure to discover OpenSearch configuration due to unrecognized response when using URL'() {
        setup:
        discoverOpenSearch = new DiscoverOpenSearchSource(prepareOpenSearchSourceUtils(200, badResponseBody, true))

        when:
        def report = discoverOpenSearch.execute(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint with bad HTTP status code received'() {
        setup:
        discoverOpenSearch = new DiscoverOpenSearchSource(prepareOpenSearchSourceUtils(500, osResponseBody, true))

        when:
        def report = discoverOpenSearch.execute(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint if no pre-formatted URLs work when discovering with host+port'() {
        setup:
        discoverOpenSearch = new DiscoverOpenSearchSource(prepareOpenSearchSourceUtils(200, osResponseBody, false))

        when:
        def report = discoverOpenSearch.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == HOST_FIELD_PATH
    }

    def 'Fail when missing required fields'() {
        setup:
        discoverOpenSearch = new DiscoverOpenSearchSource()

        when:
        def report = discoverOpenSearch.execute(null, FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.getErrorMessages()*.getPath() == [URL_FIELD_PATH]
    }

    def 'Returns all the possible error codes correctly'() {
        setup:
        discoverOpenSearch = new DiscoverOpenSearchSource()
        DiscoverOpenSearchSource cannotConnectOpenSearch = new DiscoverOpenSearchSource(prepareOpenSearchSourceUtils(200, osResponseBody, false))
        DiscoverOpenSearchSource unknownEndpointOpenSearch = new DiscoverOpenSearchSource(prepareOpenSearchSourceUtils(200, badResponseBody, true))

        when:
        def errorCodes = discoverOpenSearch.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectOpenSearch.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def unknownEndpointReport = unknownEndpointOpenSearch.execute(getBaseDiscoverByUrlArgs(TEST_OPEN_SEARCH_URL), FUNCTION_PATH)

        then:
        errorCodes.size() == 2
        errorCodes.contains(cannotConnectReport.getErrorMessages()[0].getCode())
        errorCodes.contains(unknownEndpointReport.getErrorMessages()[0].getCode())
    }

    def prepareOpenSearchSourceUtils(int statusCode, String responseBody, boolean endpointIsReachable) {
        final clientFactoryFactory = Mock(ClientFactoryFactory) {
            final secureCxfClientFactory = Mock(SecureCxfClientFactory) {
                getWebClient() >> mockWebClient(statusCode, responseBody)
            }

            getSecureCxfClientFactory(_ as String, _ as Class) >> secureCxfClientFactory
            getSecureCxfClientFactory(_ as String, _ as Class, _ as String, _ as String) >> secureCxfClientFactory
        }

        def sourceUtilCommons = new SourceUtilCommons(Mock(ConfiguratorSuite))
        def requestUtils = new TestRequestUtils(clientFactoryFactory, endpointIsReachable)

        return new OpenSearchSourceUtils(requestUtils, sourceUtilCommons)
    }

    def mockWebClient(int statusCode, String responseBody) {
        return Mock(WebClient) {
            final mockResponse = Mock(Response) {
                getStatus() >> statusCode
                readEntity(String.class) >> responseBody
                getMediaType() >> Mock(MediaType) {
                    toString() >> "text/xml"
                }
            }

            get() >> mockResponse
            post(_ as Object) >> mockResponse
        }
    }

    static class TestRequestUtils extends RequestUtils {

        Boolean endpointIsReachable

        TestRequestUtils(ClientFactoryFactory clientFactoryFactory, Boolean isReachable) {
            super(clientFactoryFactory)
            endpointIsReachable = isReachable
        }

        @Override
        Report<Void> endpointIsReachable(UrlField urlField) {
            if (endpointIsReachable == null) {
                return super.endpointIsReachable(urlField)
            }

            Report report = Reports.emptyReport()
            if (!endpointIsReachable) {
                report.addErrorMessage(new ErrorMessageImpl(TEST_ERROR_CODE))
            }
            return report
        }
    }
}
