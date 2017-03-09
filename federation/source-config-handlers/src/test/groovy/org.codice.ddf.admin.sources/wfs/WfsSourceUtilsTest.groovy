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
 */
package org.codice.ddf.admin.sources.wfs

import org.codice.ddf.admin.api.config.sources.WfsSourceConfiguration
import org.codice.ddf.admin.api.handler.ConfigurationMessage
import org.codice.ddf.admin.api.handler.report.ProbeReport
import org.codice.ddf.admin.api.services.WfsServiceProperties
import org.codice.ddf.admin.commons.requests.RequestUtils
import org.codice.ddf.admin.commons.sources.SourceHandlerCommons
import spock.lang.Shared
import spock.lang.Specification

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.MessageType.FAILURE
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.MessageType.SUCCESS

class WfsSourceUtilsTest extends Specification {

    WfsSourceUtils utils
    RequestUtils requestUtils

    static TEST_URL = "testUrl"
    def TEST_USER = "testUser"
    def TEST_PW = "hunter2"
    def TEST_HOST = "testHost"
    def TEST_PORT = 1234

    @Shared xml10 = this.getClass().getClassLoader().getResource('wfs10GetCapabilities.xml').text
    @Shared xml20 = this.getClass().getClassLoader().getResource('wfs20GetCapabilities.xml').text
    @Shared xmlBad = this.getClass().getClassLoader().getResource('unsupportedWfsGetCapabilities.xml').text
    @Shared plntxt = this.getClass().getClassLoader().getResource('plainText.txt').text

    def setup() {
        requestUtils = Mock(RequestUtils)
        utils = Spy(WfsSourceUtils, constructorArgs : [requestUtils])
    }

    def 'test discoverWfsUrl good args'() {
        setup:
        def report = Mock(ProbeReport) {
            1 * containsFailureMessages() >> false
            1 * getProbeResult(SourceHandlerCommons.DISCOVERED_URL) >> TEST_URL
        }
        utils.sendWfsCapabilitiesRequest(_, TEST_USER, TEST_PW) >> report

        when:
        def result = utils.discoverWfsUrl(TEST_HOST, TEST_PORT, TEST_USER, TEST_PW)

        then:
        report == result
        result.getProbeResult(SourceHandlerCommons.DISCOVERED_URL) == TEST_URL
    }

    def 'test discoverWfsUrl bad args'() {
        setup:
        def report = Mock(ProbeReport) {
            containsFailureMessages() >> true
        }
        utils.sendWfsCapabilitiesRequest(_, TEST_USER, TEST_PW) >> report

        when:
        def result = utils.discoverWfsUrl(TEST_HOST, TEST_PORT, TEST_USER, TEST_PW)

        then:
        result.messages().find {it.type() == FAILURE && it.subtype() == SourceHandlerCommons.UNKNOWN_ENDPOINT}
    }

    def 'test sendWfsCapabilitiesRequest request failure'() {
        setup:
        def report = Mock(ProbeReport) {
            containsFailureMessages() >> true
        }
        requestUtils.sendGetRequest(_, TEST_USER, TEST_PW) >> report

        when:
        def result = utils.sendWfsCapabilitiesRequest(TEST_URL, TEST_USER, TEST_PW)

        then:
        result == report
        result.containsFailureMessages()
    }

    def 'test sendWfsCapabilitiesRequest with bad response'(code, type) {
        setup:
        def response = Spy(ProbeReport) {
            containsFailureMessages() >> false
            getProbeResult(RequestUtils.STATUS_CODE) >> code
            getProbeResult(RequestUtils.CONTENT_TYPE) >> type
        }
        requestUtils.sendGetRequest(_,TEST_USER, TEST_PW) >> response

        when:
        def result = utils.sendWfsCapabilitiesRequest(TEST_URL, TEST_USER, TEST_PW)

        then:
        result == response
        result.messages().find {it.type() == FAILURE && it.subtype() == SourceHandlerCommons.UNKNOWN_ENDPOINT}

        where:
        code    | type
        401     | "text/xml"
        405     | "application/xml"
        404     | "text/xml; charset=UTF-8"
        200     | "application/json"
        200     | "image/jpg"
        200     | "application/xml; charset=UTF-16"
    }

    def 'test sendWfsCapabilitiesRequest with xml'() {
        setup:
        def report = Spy(ProbeReport) {
            containsFailureMessages() >> false
            getProbeResult(RequestUtils.STATUS_CODE) >> 200
            getProbeResult(RequestUtils.CONTENT_TYPE) >> "text/xml"
        }
        requestUtils.sendGetRequest(_, TEST_USER, TEST_PW) >> report

        when:
        def result = utils.sendWfsCapabilitiesRequest(TEST_URL, TEST_USER, TEST_PW)

        then:
        result.messages().find {it.type() == SUCCESS && it.subtype() == SourceHandlerCommons.VERIFIED_CAPABILITIES}
        result.getProbeResult(SourceHandlerCommons.DISCOVERED_URL) == TEST_URL
    }

    def 'test getPreferredWfsConfig bad request'() {
        setup:
        def report = Mock(ProbeReport) {
            containsFailureMessages() >> true
        }
        utils.sendWfsCapabilitiesRequest(_, TEST_USER, TEST_PW) >> report

        when:
        def result = utils.getPreferredWfsConfig(TEST_URL, TEST_USER, TEST_PW)

        then:
        result == report
        result.containsFailureMessages()
    }

    def 'test getPreferredWfsConfig with good xml'(xml, factoryPid) {
        setup:
        def report = Mock(ProbeReport) {
            containsFailureMessages() >> false
            getProbeResult(RequestUtils.CONTENT) >> xml
        }
        utils.sendWfsCapabilitiesRequest(_, TEST_USER, TEST_PW) >> report

        when:
        def result = utils.getPreferredWfsConfig(TEST_URL, TEST_USER, TEST_PW)
        WfsSourceConfiguration config = result.getProbeResult(SourceHandlerCommons.DISCOVERED_SOURCES)

        then:
        result.messages().find {it.type() == SUCCESS && it.subtype() == SourceHandlerCommons.DISCOVERED_SOURCE}
        config.sourceUserName() == TEST_USER
        config.sourceUserPassword() == TEST_PW
        config.endpointUrl() == TEST_URL
        config.factoryPid() == factoryPid

        where:
        xml     | factoryPid
        xml10   | WfsServiceProperties.WFS1_FACTORY_PID
        xml20   | WfsServiceProperties.WFS2_FACTORY_PID
    }

    def 'test getPreferredWfsConfig failure cases'(input, subtype) {
        setup:
        def report = Mock(ProbeReport) {
            containsFailureMessages() >> false
            getProbeResult(RequestUtils.CONTENT) >> input
        }
        utils.sendWfsCapabilitiesRequest(_, TEST_USER, TEST_PW) >> report

        when:
        def result = utils.getPreferredWfsConfig(TEST_URL, TEST_USER, TEST_PW)

        then:
        result.messages().find {it.type() == FAILURE && it.subtype() == subtype}

        where:
        input   | subtype
        xmlBad  | SourceHandlerCommons.UNKNOWN_ENDPOINT
        plntxt  | ConfigurationMessage.INTERNAL_ERROR
    }
}