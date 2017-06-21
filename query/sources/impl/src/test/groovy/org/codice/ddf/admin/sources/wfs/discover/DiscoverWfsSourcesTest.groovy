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
import org.codice.ddf.admin.common.fields.common.ResponseField
import org.codice.ddf.admin.common.report.ReportWithResultImpl
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import org.codice.ddf.admin.sources.wfs.WfsSourceUtils
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverWfsSourcesTest extends Specification {

    DiscoverWfsSource discoverWfs

    WfsSourceUtils wfsSourceUtils

    RequestUtils requestUtils

    static TEST_WFS_URL = 'http://localhost:8080/geoserver/wfs'

    static WFS_10_GET_CAPABILITIES_FILE_PATH = 'responses/wfs/wfs10GetCapabilities.xml'

    static WFS_20_GET_CAPABILITIES_FILE_PATH = 'responses/wfs/wfs20GetCapabilities.xml'

    static UNRECOGNIZED_GET_CAPABILITIES_FILE_PATH = 'responses/wfs/unsupportedVersionGetCapabilities.xml'

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
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, WFS_10_GET_CAPABILITIES_FILE_PATH, 200, TEST_WFS_URL)
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
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, WFS_20_GET_CAPABILITIES_FILE_PATH, 200, TEST_WFS_URL)
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
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, WFS_10_GET_CAPABILITIES_FILE_PATH, 200, TEST_WFS_URL)
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
        1 * requestUtils.discoverUrlFromHost(_, _, _, _) >> createResponseFieldResult(false, WFS_20_GET_CAPABILITIES_FILE_PATH, 200, TEST_WFS_URL)
        config.endpointUrl() == TEST_WFS_URL
        config.wfsVersion() == WfsVersion.WFS_VERSION_2
    }

    def 'Unknown endpoint error when unrecognized WFS version is received'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, UNRECOGNIZED_GET_CAPABILITIES_FILE_PATH, 200, TEST_WFS_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error when bad HTTP code received'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, WFS_20_GET_CAPABILITIES_FILE_PATH, 500, TEST_WFS_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == URL_FIELD_PATH
    }

    def 'Unknown endpoint error when unrecognized response received'() {
        setup:
        discoverWfs.setValue(getBaseDiscoverByUrlArgs(TEST_WFS_URL))

        when:
        def report = discoverWfs.getValue()

        then:
        1 * requestUtils.sendGetRequest(_, _, _) >> createResponseFieldResult(false, 'responses/badResponse.xml', 200, TEST_WFS_URL)
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.UNKNOWN_ENDPOINT
        report.messages()[0].getPath() == URL_FIELD_PATH
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
