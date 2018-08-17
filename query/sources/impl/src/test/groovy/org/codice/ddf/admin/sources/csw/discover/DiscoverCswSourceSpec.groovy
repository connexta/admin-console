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

import org.apache.cxf.jaxrs.client.WebClient
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.report.Reports
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.sources.csw.CswSourceUtils
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import org.codice.ddf.cxf.client.ClientFactoryFactory
import org.codice.ddf.cxf.client.SecureCxfClientFactory
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import spock.lang.Shared

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class DiscoverCswSourceSpec extends SourceCommonsSpec {

    static final List<Object> FUNCTION_PATH = [DiscoverCswSource.FIELD_NAME]

    static TEST_CSW_URL = 'https://testHostName:12345/services/csw'

    static NO_FILTER = 'NO_FILTER'

    static BASE_PATH = [DiscoverCswSource.FIELD_NAME]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOST_FIELD_PATH = [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

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

    DiscoverCswSource discoverCsw

    def 'Successfully discover DDF federation profile with URL'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, ddfCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE
        config.outputSchema() == CswSourceUtils.METACARD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
        config.credentials().password() == ServiceCommons.FLAG_PASSWORD
    }

    def 'Successfully discover CSWSpecification federation profile with URL'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, specCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CswFederatedSource.CSW_SPEC_PROFILE_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.CSW_2_0_2_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover GMD CSW federation profile with URL'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, gmdCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.GmdCswFederatedSource.GMD_CSW_ISO_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.GMD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover DDF federation profile using hostname and port'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, ddfCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.cswProfile() == CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE
        config.outputSchema() == CswSourceUtils.METACARD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
        config.credentials().password() == FLAG_PASSWORD
    }


    def 'Successfully discover CSWSpecification federation profile using hostname and port'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, specCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.cswProfile() == CswProfile.CswFederatedSource.CSW_SPEC_PROFILE_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.CSW_2_0_2_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover GMD CSW federation profile using hostname and port'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, gmdCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.cswProfile() == CswProfile.GmdCswFederatedSource.GMD_CSW_ISO_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.GMD_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Unknown endpoint error with bad HTTP status'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(500, gmdCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error with unrecognized response when using URL'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, badResponseBody, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error with unrecognized response when using hostname+port'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, badResponseBody, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == HOST_FIELD_PATH
    }

    def 'Unknown endpoint if no pre-formatted URLs work when discovering with host+port'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, badResponseBody, false))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()
    }

    def 'Unrecognized CSW output schema defaults to CSW 2.0.2 schema'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, unrecognizedCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_CSW_URL
        config.cswProfile() == CswProfile.CswFederatedSource.CSW_SPEC_PROFILE_FEDERATED_SOURCE
        config.outputSchema() == CswSourceUtils.CSW_2_0_2_OUTPUT_SCHEMA
        config.spatialOperator() == NO_FILTER
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Unknown endpoint error when there is no output schema'() {
        setup:
        discoverCsw = new DiscoverCswSource(prepareCswSourceUtils(200, noOutputSchemaCswResponse, true))

        when:
        def report = discoverCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Fail when missing required fields'() {
        setup:
        discoverCsw = new DiscoverCswSource()

        when:
        def report = discoverCsw.execute(null, FUNCTION_PATH)

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
        discoverCsw = new DiscoverCswSource()
        DiscoverCswSource cannotConnectCsw = new DiscoverCswSource(prepareCswSourceUtils(200, badResponseBody, false))
        DiscoverCswSource unknownEndpointCsw = new DiscoverCswSource(prepareCswSourceUtils(200, noOutputSchemaCswResponse, true))

        when:
        def errorCodes = discoverCsw.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectCsw.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def unknownEndpointReport = unknownEndpointCsw.execute(getBaseDiscoverByUrlArgs(TEST_CSW_URL), FUNCTION_PATH)

        then:
        errorCodes.size() == 2
        errorCodes.contains(cannotConnectReport.getErrorMessages()[0].getCode())
        errorCodes.contains(unknownEndpointReport.getErrorMessages()[0].getCode())
    }

    def prepareCswSourceUtils(int statusCode, String responseBody, boolean endpointIsReachable) {
        final clientFactoryFactory = Mock(ClientFactoryFactory) {
            final secureCxfClientFactory = Mock(SecureCxfClientFactory) {
                getWebClient() >> mockWebClient(statusCode, responseBody)
            }

            getSecureCxfClientFactory(_ as String, _ as Class) >> secureCxfClientFactory
            getSecureCxfClientFactory(_ as String, _ as Class, _ as String, _ as String) >> secureCxfClientFactory
        }

        def sourceUtilCommons = new SourceUtilCommons(Mock(ConfiguratorSuite))
        def requestUtils = new TestRequestUtils(clientFactoryFactory, endpointIsReachable)

        return new CswSourceUtils(sourceUtilCommons, requestUtils)
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
