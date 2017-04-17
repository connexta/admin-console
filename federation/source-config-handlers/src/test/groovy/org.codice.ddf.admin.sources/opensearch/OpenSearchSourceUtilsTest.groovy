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
package org.codice.ddf.admin.sources.opensearch

import spock.lang.Specification

class OpenSearchSourceUtilsTest extends Specification {

//    OpenSearchSourceUtils utils
//    RequestUtils requestUtils
//    static TEST_URL = "testUrl"
//    def TEST_USER = "testUser"
//    def TEST_PW = "hunter2"
//    def TEST_HOST = "testHost"
//    def TEST_PORT = 1234
//
//    @Shared goodXml = this.getClass().getClassLoader().getResource('goodOSQueryResponse.xml').text
//    @Shared badXml = this.getClass().getClassLoader().getResource('badOSQueryResponse.xml').text
//    @Shared plnTxt = this.getClass().getClassLoader().getResource('plainText.txt').text
//
//    def setup() {
//        requestUtils = Mock(RequestUtils)
//        utils = Spy(OpenSearchSourceUtils, constructorArgs: [requestUtils])
//    }
//
//    def 'test getOpenSearchConfig good endpoint'() {
//        setup:
//        def report = Spy(ProbeReport) {
//            1 * containsFailureMessages() >> false
//        }
//        utils.verifyOpenSearchCapabilities(TEST_URL, TEST_USER, TEST_PW) >> report
//
//        when:
//        def result = utils.getOpenSearchConfig(TEST_URL, TEST_USER, TEST_PW)
//        OpenSearchSourceConfiguration config = result.getProbeResult(SourceHandlerCommons.DISCOVERED_SOURCES)
//
//        then:
//        result == report
//        config.sourceUserName() == TEST_USER
//        config.sourceUserPassword() == TEST_PW
//        config.endpointUrl() == TEST_URL
//        config.factoryPid() == OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID
//        config.configurationHandlerId() == OpenSearchSourceConfigurationHandler.OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID
//    }
//
//    def 'test getOpenSearchConfig bad endpoint'() {
//        setup:
//        def report = Mock(ProbeReport) {
//            1 * containsFailureMessages() >> true
//        }
//        utils.verifyOpenSearchCapabilities(TEST_URL, TEST_USER, TEST_PW) >> report
//
//        when:
//        def result = utils.getOpenSearchConfig(TEST_URL, TEST_USER, TEST_PW)
//
//        then:
//        0 * report.probeResult(_,_)
//        result == report
//    }
//
//    def 'test discoverOpenSearchUrl good args'() {
//        setup:
//        def report = Mock(ProbeReport) {
//            1 * containsFailureMessages() >> false
//            1 * getProbeResult(SourceHandlerCommons.DISCOVERED_URL) >> TEST_URL
//        }
//        utils.verifyOpenSearchCapabilities(_, TEST_USER, TEST_PW) >> report
//
//        when:
//        def result = utils.discoverOpenSearchUrl(TEST_HOST, TEST_PORT, TEST_USER, TEST_PW)
//
//        then:
//        report == result
//        result.getProbeResult(SourceHandlerCommons.DISCOVERED_URL) == TEST_URL
//    }
//
//    def 'test discoverOpenSearchUrl bad args'() {
//        setup:
//        def report = Mock(ProbeReport) {
//            containsFailureMessages() >> true
//        }
//        utils.verifyOpenSearchCapabilities(_, TEST_USER, TEST_PW) >> report
//
//        when:
//        def result = utils.discoverOpenSearchUrl(TEST_HOST, TEST_PORT, TEST_USER, TEST_PW)
//
//        then:
//        result.messages().find {it.type() == FAILURE && it.subtype() == SourceHandlerCommons.UNKNOWN_ENDPOINT}
//    }
//
//    def 'test verifyOpenSearchCapabilities request failure'() {
//        setup:
//        def response = Mock(ProbeReport) {
//            containsFailureMessages() >> true
//        }
//        requestUtils.sendGetRequest(_, , TEST_USER, TEST_PW) >> response
//
//        when:
//        def result = utils.verifyOpenSearchCapabilities(TEST_URL, TEST_USER, TEST_PW)
//
//        then:
//        result == response
//        result.containsFailureMessages()
//    }
//
//    def 'test verifyOpenSearchCapabilities bad responses'(code, type) {
//        setup:
//        def report = Spy(ProbeReport) {
//            containsFailureMessages() >> false
//            getProbeResult(RequestUtils.STATUS_CODE) >> code
//            getProbeResult(RequestUtils.CONTENT_TYPE) >> type
//        }
//        requestUtils.sendGetRequest(_, , TEST_USER, TEST_PW) >> report
//
//        when:
//        def result = utils.verifyOpenSearchCapabilities(TEST_URL, TEST_USER, TEST_PW)
//
//        then:
//        result == report
//        result.messages().find {it.type() == FAILURE && it.subtype() == SourceHandlerCommons.UNKNOWN_ENDPOINT}
//
//        where:
//        code    | type
//        401     | "application/atom+xml"
//        405     | "application/atom+xml; charset=UTF-8"
//        500     | "application/atom+xml"
//        200     | "application/json"
//        200     | "text/xml"
//        200     | "image/gif"
//    }
//
//    def 'test verifyOpenSearchCapabilities with xml'(xml, type, subtype, url) {
//        setup:
//        def report = Spy(ProbeReport) {
//            containsFailureMessages() >> false
//            getProbeResult(RequestUtils.STATUS_CODE) >> 200
//            getProbeResult(RequestUtils.CONTENT_TYPE) >> "application/atom+xml"
//            getProbeResult(RequestUtils.CONTENT) >> xml
//        }
//        requestUtils.sendGetRequest(_, , TEST_USER, TEST_PW) >> report
//
//        when:
//        def result = utils.verifyOpenSearchCapabilities(TEST_URL, TEST_USER, TEST_PW)
//
//        then:
//        result.messages().find {it.type() == type && it.subtype() == subtype}
//        result.getProbeResult(SourceHandlerCommons.DISCOVERED_URL) == url
//
//        where:
//        xml     | type      | subtype                                    | url
//        plnTxt  | FAILURE   | ConfigurationMessage.INTERNAL_ERROR        | null
//        badXml  | FAILURE   | SourceHandlerCommons.UNKNOWN_ENDPOINT      | null
//        goodXml | SUCCESS   | SourceHandlerCommons.VERIFIED_CAPABILITIES | TEST_URL
//    }
}