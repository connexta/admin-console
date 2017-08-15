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

import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.admin.sources.wfs.WfsSourceUtils
import spock.lang.Shared

class DiscoverWfsSourcesSpec extends SourceCommonsSpec {

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

    static BASE_PATH = [DiscoverWfsSource.FIELD_NAME, FunctionField.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOST_FIELD_PATH = [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def setup() {
        discoverWfs = new DiscoverWfsSource(Mock(ConfiguratorSuite))
    }

    def 'Successfully discover WFS 1.0.0 configuration using URL'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, wfs10ResponseBody, true))
        discoverWfs.setArguments(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.execute()
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.Wfs1.WFS_VERSION_1
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover WFS 2.0.0 configuration using URL'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, wfs20ResponseBody, true))
        discoverWfs.setArguments(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.execute()
        def config = report.getResult()

        then:
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.Wfs2.WFS_VERSION_2
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover WFS 1.0.0 configuration using hostname and port'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, wfs10ResponseBody, true))
        discoverWfs.setArguments(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverWfs.execute()
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.wfsVersion() == WfsVersion.Wfs1.WFS_VERSION_1
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Successfully discover WFS 2.0.0 configuration using hostname and port'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, wfs20ResponseBody, true))
        discoverWfs.setArguments(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverWfs.execute()
        def config = report.getResult()

        then:
        !config.endpointUrl().isEmpty()
        config.wfsVersion() == WfsVersion.Wfs2.WFS_VERSION_2
        config.credentials().password() == FLAG_PASSWORD
    }

    def 'Unknown endpoint error when unrecognized WFS version is received'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, wfsUnrecognizedResponseBody, true))
        discoverWfs.setArguments(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error when bad HTTP code received'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(500, wfs20ResponseBody, true))
        discoverWfs.setArguments(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error when unrecognized response received'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, badResponseBody, true))
        discoverWfs.setArguments(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint if no pre-formatted URLs work when discovering with host+port'() {
        setup:
        discoverWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, badResponseBody, false))
        discoverWfs.setArguments(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverWfs.execute()

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.getErrorMessages()[0].getPath() == HOST_FIELD_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = discoverWfs.execute()

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
        DiscoverWfsSource cannotConnectWfs = new DiscoverWfsSource(Mock(ConfiguratorSuite))
        cannotConnectWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, badResponseBody, false))
        cannotConnectWfs.setArguments(getBaseDiscoverByAddressArgs())

        DiscoverWfsSource unknownEndpointWfs = new DiscoverWfsSource(Mock(ConfiguratorSuite))
        unknownEndpointWfs.setWfsSourceUtils(prepareOpenSearchSourceUtils(200, badResponseBody, true))
        unknownEndpointWfs.setArguments(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def errorCodes = discoverWfs.getFunctionErrorCodes()
        def cannotConnectReport = cannotConnectWfs.execute()
        def unknownEndpointReport = unknownEndpointWfs.execute()

        then:
        errorCodes.size() == 2
        errorCodes.contains(cannotConnectReport.getErrorMessages()[0].getCode())
        errorCodes.contains(unknownEndpointReport.getErrorMessages()[0].getCode())
    }

    def prepareOpenSearchSourceUtils(int statusCode, String responseBody, boolean endpointIsReachable) {
        def requestUtils = new TestRequestUtils(createMockWebClientBuilder(statusCode, responseBody), endpointIsReachable)
        def wfsUtils = new WfsSourceUtils(Mock(ConfiguratorSuite))
        wfsUtils.setRequestUtils(requestUtils)
        return wfsUtils
    }
}
