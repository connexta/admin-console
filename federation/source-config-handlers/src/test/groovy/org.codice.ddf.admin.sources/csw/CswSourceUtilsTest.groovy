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
package org.codice.ddf.admin.sources.csw

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration
import org.codice.ddf.admin.api.handler.report.ProbeReport
import org.codice.ddf.admin.api.services.CswServiceProperties
import org.codice.ddf.admin.commons.requests.RequestUtils
import org.codice.ddf.admin.commons.sources.SourceHandlerCommons
import spock.lang.Shared
import spock.lang.Specification

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.MessageType.FAILURE
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.MessageType.SUCCESS

class CswSourceUtilsTest extends Specification {
    CswSourceUtils utils
    RequestUtils requestUtils

    @Shared
    metacardXml = this.getClass().getClassLoader().getResource('metacardGetCapabilities.xml').text
    @Shared
    gmdXml = this.getClass().getClassLoader().getResource('gmdGetCapabilities.xml').text
    @Shared
    specXml = this.getClass().getClassLoader().getResource('specGetCapabilities.xml').text

    def setup() {
        requestUtils = Mock(RequestUtils)
        utils = Spy(CswSourceUtils, constructorArgs: [requestUtils])
    }

    def 'test sendCswCapabilitiesRequest with failed request'() {
        setup:
        def report = Mock(ProbeReport)

        when:
        def result = utils.sendCswCapabilitiesRequest("testUrl", null, null)

        then:
        requestUtils.sendGetRequest(_, _, _) >> report
        report.containsFailureMessages() >> true
        result == report
    }

    def 'test sendCswCapabilitiesRequest with bad response '(code, contentType) {
        setup:
        def report = Spy(ProbeReport)

        when:
        def result = utils.sendCswCapabilitiesRequest("testUrl", null, null)

        then:
        requestUtils.sendGetRequest(_,_,_) >> report
        report.containsFailureMessages() >> false
        report.getProbeResult(RequestUtils.STATUS_CODE) >> code
        report.getProbeResult(RequestUtils.CONTENT_TYPE) >> contentType

        result.messages().find {it.type() == FAILURE && it.subtype() == SourceHandlerCommons.UNKNOWN_ENDPOINT}

        where:
        code << [401, 200]
        contentType << ["text/xml", "application/json"]
    }

    def 'test sendCswCapabilitiesRequest with good response'() {
        setup:
        def report = Spy(ProbeReport)

        when:
        def result = utils.sendCswCapabilitiesRequest("testUrl", null, null)

        then:
        requestUtils.sendGetRequest(_,_,_) >> report
        report.containsFailureMessages() >> false
        report.getProbeResult(RequestUtils.STATUS_CODE) >> 200
        report.getProbeResult(RequestUtils.CONTENT_TYPE) >> "application/xml"

        result.getProbeResult(SourceHandlerCommons.DISCOVERED_URL) == "testUrl"
        !result.containsFailureMessages()
        result.messages().find {it.type() == SUCCESS && it.subtype() == SourceHandlerCommons.VERIFIED_CAPABILITIES}
    }

    def 'test discoverCswUrl success'() {
        setup:
        def report = Mock(ProbeReport)

        when:
        def result = utils.discoverCswUrl("test", 1234, null, null)

        then:
        utils.sendCswCapabilitiesRequest(_,_,_) >> report
        report.containsFailureMessages() >> false
        result == report
    }

    def 'test discoverCswUrl failure'() {
        setup:
        def report = Mock(ProbeReport)

        when:
        def result = utils.discoverCswUrl("test", 1234, null, null)

        then:
        utils.sendCswCapabilitiesRequest(_,_,_) >> report
        report.containsFailureMessages() >> true

        result.containsFailureMessages()
        result.messages().find {it.type() == FAILURE && it.subtype() == SourceHandlerCommons.UNKNOWN_ENDPOINT}
    }

    def 'test getPreferredCswConfig config'(xml, factoryPid, schema) {
        setup:
        def report = Mock(ProbeReport) {
            1 * containsFailureMessages() >> false
            1 * getProbeResult(RequestUtils.CONTENT) >> xml
        }
        utils.sendCswCapabilitiesRequest(_, _, _) >> report

        when:
        def result = utils.getPreferredCswConfig("testUrl", "testUser", "hunter2")
        CswSourceConfiguration config = result.getProbeResult(SourceHandlerCommons.DISCOVERED_SOURCES)

        then:
        !result.containsFailureMessages()
        result.messages().find {it.type() == SUCCESS && it.subtype() == SourceHandlerCommons.DISCOVERED_SOURCE}
        config.outputSchema() == schema
        config.factoryPid() == factoryPid
        config.endpointUrl() == "testUrl"
        config.sourceUserName() == "testUser"
        config.sourceUserPassword() == "hunter2"
        config.configurationHandlerId() == CswSourceConfigurationHandler.CSW_SOURCE_CONFIGURATION_HANDLER_ID

        where:
        xml         | factoryPid                                    | schema
        metacardXml | CswServiceProperties.CSW_PROFILE_FACTORY_PID  | null
        gmdXml      | CswServiceProperties.CSW_GMD_FACTORY_PID      | CswSourceUtils.GMD_OUTPUT_SCHEMA
        specXml     | CswServiceProperties.CSW_SPEC_FACTORY_PID     | "http://www.opengis.net/cat/csw/2.0.2"
    }

}