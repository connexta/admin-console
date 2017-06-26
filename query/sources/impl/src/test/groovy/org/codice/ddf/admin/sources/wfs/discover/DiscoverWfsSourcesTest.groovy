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

import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.fields.common.HostField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import org.codice.ddf.admin.sources.wfs.WfsSourceUtils
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverWfsSourcesTest extends Specification {

    @Shared
            wfs10ResponseBody = this.getClass().getClassLoader().getResource('responses/wfs/wfs10GetCapabilities.xml').text

    @Shared
            wfs20ResponseBody = this.getClass().getClassLoader().getResource('responses/wfs/wfs20GetCapabilities.xml').text

    @Shared
            wfsUnrecognizedResponseBody = this.getClass().getClassLoader().getResource('responses/wfs/unsupportedVersionGetCapabilities.xml').text

    @Shared
            badResponseBody = this.getClass().getClassLoader().getResource('responses/badResponse.xml').text

    DiscoverWfsSource discoverWfs

    WfsSourceUtils wfsSourceUtils

    RequestUtils requestUtils

    static TEST_WFS_URL = 'http://localhost:8080/geoserver/wfs'

    static BASE_PATH = [DiscoverWfsSource.FIELD_NAME, FunctionField.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def setup() {
        requestUtils = Mock(RequestUtils)
        wfsSourceUtils = new WfsSourceUtils(requestUtils, new SourceUtilCommons())
        discoverWfs = new DiscoverWfsSource(wfsSourceUtils)
    }

    def 'Successfully discover WFS 1.0.0 configuration using URL'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()
        def config = (WfsSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, wfs10ResponseBody, 200, TEST_WFS_URL)
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.WFS_VERSION_1
    }

    def 'Successfully discover WFS 2.0.0 configuration using URL'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()
        def config = (WfsSourceConfigurationField) report.result()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, wfs20ResponseBody, 200, TEST_WFS_URL)
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.WFS_VERSION_2
    }

    def 'Successfully discover WFS 1.0.0 configuration using hostname and port'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverWfs.getValue()
        def config = (WfsSourceConfigurationField) report.result()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, wfs10ResponseBody, 200, TEST_WFS_URL)
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.WFS_VERSION_1
    }

    def 'Successfully discover WFS 2.0.0 configuration using hostname and port'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverWfs.getValue()
        def config = (WfsSourceConfigurationField) report.result()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, wfs20ResponseBody, 200, TEST_WFS_URL)
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.WFS_VERSION_2
    }

    def 'Unknown endpoint error when unrecognized WFS version is received'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, wfsUnrecognizedResponseBody, 200, TEST_WFS_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverWfsSource.FIELD_NAME]
    }

    def 'Unknown endpoint error when bad HTTP code received'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, wfs20ResponseBody, 500, TEST_WFS_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverWfsSource.FIELD_NAME]
    }

    def 'Unknown endpoint error when unrecognized response received'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, badResponseBody, 200, TEST_WFS_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == [DiscoverWfsSource.FIELD_NAME]
    }

    @Ignore
    // TODO: 6/22/17 phuffer - Fix this test and the way we are mocking out RequestUtils
    def 'Cannot connect if errors from discover url from host'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByAddressArgs())

        when:
        def report = discoverWfs.getValue()

        then:
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(true, "", 0, "")
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.CANNOT_CONNECT
        report.messages()[0].getPath() == [ADDRESS_FIELD_PATH, HostField.DEFAULT_FIELD_NAME].flatten()
    }

    def 'Fail when missing required fields'() {
        when:
        def report = discoverWfs.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [URL_FIELD_PATH]
    }
}
