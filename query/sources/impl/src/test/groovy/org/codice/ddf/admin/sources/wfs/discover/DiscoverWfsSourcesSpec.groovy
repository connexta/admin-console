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
package org.codice.ddf.admin.sources.wfs.discover

import org.apache.cxf.jaxrs.client.WebClient
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.report.Reports
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import org.codice.ddf.admin.sources.wfs.WfsSourceUtils
import org.codice.ddf.cxf.client.ClientFactoryFactory
import org.codice.ddf.cxf.client.SecureCxfClientFactory
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import spock.lang.Shared

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class DiscoverWfsSourcesSpec extends SourceCommonsSpec {

    static final List<Object> FUNCTION_PATH = [DiscoverWfsSource.FIELD_NAME]

    @Shared
            wfs10ResponseBody = this.getClass().getClassLoader().getResource('responses/wfs/wfs10GetCapabilities.xml').text

    @Shared
            wfs20ResponseBody = this.getClass().getClassLoader().getResource('responses/wfs/wfs20GetCapabilities.xml').text

    @Shared
            wfsUnrecognizedResponseBody = this.getClass().getClassLoader().getResource('responses/wfs/unsupportedVersionGetCapabilities.xml').text

    @Shared
            badResponseBody = this.getClass().getClassLoader().getResource('responses/badResponse.xml').text

    DiscoverWfsSource discoverWfs

    static TEST_WFS_URL = 'https://testHostName:12345/services/wfs'

    static BASE_PATH = [DiscoverWfsSource.FIELD_NAME]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOST_FIELD_PATH = [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def 'Successfully discover WFS 1.0.0 configuration using URL'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, wfs10ResponseBody, true))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByUrlArgs(TEST_WFS_URL), FUNCTION_PATH)
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.Wfs1.WFS_VERSION_1
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover WFS 2.0.0 configuration using URL'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, wfs20ResponseBody, true))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByUrlArgs(TEST_WFS_URL), FUNCTION_PATH)
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.Wfs2.WFS_VERSION_2
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover WFS 1.0.0 configuration using hostname and port'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, wfs10ResponseBody, true))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.wfsVersion() == WfsVersion.Wfs1.WFS_VERSION_1
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover WFS 2.0.0 configuration using hostname and port'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, wfs20ResponseBody, true))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.wfsVersion() == WfsVersion.Wfs2.WFS_VERSION_2
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Unknown endpoint error when unrecognized WFS version is received'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, wfsUnrecognizedResponseBody, true))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByUrlArgs(TEST_WFS_URL), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error when bad HTTP code received'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(500, wfs20ResponseBody, true))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByUrlArgs(TEST_WFS_URL), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error when unrecognized response received'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, badResponseBody, true))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByUrlArgs(TEST_WFS_URL), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint if no pre-formatted URLs work when discovering with host+port'() {
        setup:
        discoverWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, badResponseBody, false))

        when:
        def report = discoverWfs.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == HOST_FIELD_PATH
    }

    def 'Fail when missing required fields'() {
        setup:
        discoverWfs = new DiscoverWfsSource()

        when:
        def report = discoverWfs.execute(null, FUNCTION_PATH)

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
        discoverWfs = new DiscoverWfsSource()
        DiscoverWfsSource cannotConnectWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, badResponseBody, false))
        DiscoverWfsSource unknownEndpointWfs = new DiscoverWfsSource(prepareWfsSourceUtils(200, badResponseBody, true))

        when:
        def errorCodes = discoverWfs.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectWfs.execute(getBaseDiscoverByAddressArgs(), FUNCTION_PATH)
        def unknownEndpointReport = unknownEndpointWfs.execute(getBaseDiscoverByUrlArgs(TEST_WFS_URL), FUNCTION_PATH)

        then:
        errorCodes.size() == 2
        errorCodes.contains(cannotConnectReport.getErrorMessages()[0].getCode())
        errorCodes.contains(unknownEndpointReport.getErrorMessages()[0].getCode())
    }

    def prepareWfsSourceUtils(int statusCode, String responseBody, boolean endpointIsReachable) {
        final clientFactoryFactory = Mock(ClientFactoryFactory) {
            final secureCxfClientFactory = Mock(SecureCxfClientFactory) {
                getWebClient() >> mockWebClient(statusCode, responseBody)
            }

            getSecureCxfClientFactory(_ as String, _ as Class) >> secureCxfClientFactory
            getSecureCxfClientFactory(_ as String, _ as Class, _ as String, _ as String) >> secureCxfClientFactory
        }

        def sourceUtilCommons = new SourceUtilCommons(Mock(ConfiguratorSuite))
        def requestUtils = new TestRequestUtils(clientFactoryFactory, endpointIsReachable)

        return new WfsSourceUtils(requestUtils, sourceUtilCommons)
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
