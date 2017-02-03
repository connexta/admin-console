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

import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.impl.client.CloseableHttpClient
import org.codice.ddf.admin.api.config.sources.WfsSourceConfiguration
import spock.lang.Specification

import javax.net.ssl.SSLPeerUnverifiedException

import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS1_FACTORY_PID
import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS2_FACTORY_PID

class WfsSourceUtilsTest extends Specification {

    def client = Mock(CloseableHttpClient)
    def response = Mock(CloseableHttpResponse)
    def statusLine = Mock(StatusLine)
    def entity = Mock(HttpEntity)
    def cType = Mock(Header)
    def configuration = Mock(WfsSourceConfiguration)

    def wfs20Xml = this.getClass().getClassLoader().getResourceAsStream('wfs20GetCapabilities.xml')
    def wfs10Xml = this.getClass().getClassLoader().getResourceAsStream('wfs10GetCapabilities.xml')
    def wfsBadXml = this.getClass().getClassLoader().getResourceAsStream('unsupportedWfsGetCapabilities.xml')

    def utils
    def setup() {
        utils = Spy(WfsSourceUtils) {
            getCloseableHttpClient(_) >> client
        }
    }

    // Tests for getUrlAvailability
    def 'test happy path trusted CA'() {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        1 * client.execute(_) >> response
        1 * response.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * response.getEntity() >> entity
        1 * entity.getContentType() >> cType
        1 * cType.getValue() >> "text/xml"

        assert urlAvail.isAvailable()
        assert !urlAvail.isCertError()
        assert urlAvail.isTrustedCertAuthority()
    }

    def 'test bad return code noTrustClient' () {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        1 * client.execute(_) >> response
        1 * response.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 405
        1 * response.getEntity() >> entity
        1 * entity.getContentType() >> cType
        1 * cType.getValue() >> "application/xml"
        assert !urlAvail.isAvailable()
        assert !urlAvail.isCertError()
        assert urlAvail.isTrustedCertAuthority()
    }

    def 'test bad mime type noTrustClient' () {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        1 * client.execute(_) >> response
        1 * response.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * response.getEntity() >> entity
        1 * entity.getContentType() >> cType
        1 * cType.getValue() >> "application/json"
        assert !urlAvail.isAvailable()
        assert !urlAvail.isCertError()
        assert urlAvail.isTrustedCertAuthority()
    }

    def 'test cert error with noTrustClient'() {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        1 * client.execute(_) >> {throw new SSLPeerUnverifiedException("test")}
        assert !urlAvail.isAvailable()
        assert urlAvail.isCertError()
        assert !urlAvail.isTrustedCertAuthority()

    }

    def 'test good path trustClient'() {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        2 * client.execute(_) >> {throw new IOException("exception")} >> response
        1 * response.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * response.getEntity() >> entity
        1 * entity.getContentType() >> cType
        1 * cType.getValue() >> "text/xml"

        assert urlAvail.isAvailable()
        assert !urlAvail.isCertError()
        assert !urlAvail.isTrustedCertAuthority()
    }

    def 'test bad return code trustClient'() {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        2 * client.execute(_) >> {throw new IOException("exception")} >> response
        1 * response.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 405
        1 * response.getEntity() >> entity
        1 * entity.getContentType() >> cType
        1 * cType.getValue() >> "application/xml"
        assert !urlAvail.isAvailable()
        assert !urlAvail.isCertError()
        assert !urlAvail.isTrustedCertAuthority()
    }

    def 'test bad mime type trustClient'() {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        2 * client.execute(_) >> {throw new IOException("exception")} >> response
        1 * response.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * response.getEntity() >> entity
        1 * entity.getContentType() >> cType
        1 * cType.getValue() >> "application/json"
        assert !urlAvail.isAvailable()
        assert !urlAvail.isCertError()
        assert !urlAvail.isTrustedCertAuthority()
    }

    def 'test failure to connect'() {
        when:
        def urlAvail = utils.getUrlAvailability("testUrl", null, null)

        then:
        2 * client.execute(_) >> {throw new IOException("exception")}
        assert !urlAvail.isAvailable()
        assert !urlAvail.isCertError()
        assert !urlAvail.isTrustedCertAuthority()
    }

    // Tests for getPreferredConfig

    def 'test empty config with empty response'() {
        when:
        def config = utils.getPreferredConfig(configuration)

        then:
        1 * client.execute(_) >> response
        1 * response.getEntity() >> entity
        1 * entity.getContent() >> ""
        config == Optional.empty()
    }

    def 'test WFS 1.0 config properly discovered'() {
        when:
        def config = utils.getPreferredConfig(configuration)

        then:
        configuration.endpointUrl() >> "test"
        1 * client.execute(_) >> response
        1 * response.getEntity() >> entity
        1 * entity.getContent() >> wfs10Xml
        assert config.isPresent()
        config.get().factoryPid() == WFS1_FACTORY_PID
    }

    def 'test WFS 2.0 config properly discovered'() {
        when:
        def config = utils.getPreferredConfig(configuration)

        then:
        configuration.endpointUrl() >> "test"
        1 * client.execute(_) >> response
        1 * response.getEntity() >> entity
        1 * entity.getContent() >> wfs20Xml
        assert config.isPresent()
        config.get().factoryPid() == WFS2_FACTORY_PID
    }

    def 'test unsupported version returns no config'() {
        when:
        def config = utils.getPreferredConfig(configuration)

        then:
        configuration.endpointUrl() >> "test"
        1 * client.execute(_) >> response
        1 * response.getEntity() >> entity
        1 * entity.getContent() >> wfsBadXml
        assert !config.isPresent()
    }

    // Tests for confirmEndpointUrl

//    def 'test no url created with bad hostname/port'() {
//        when:
//        def endpointUrl = utils.confirmEndpointUrl(configuration)
//
//        then:
//        _ * configuration.sourceHostName() >> "test"
//        _ * configuration.sourcePort() >> 443
//        _ * client.execute(_) >> {throw new IOException()}
//        endpointUrl == null
//    }
//
//    def 'test URL created with no cert error'() {
//        when:
//        def endpointUrl = utils.confirmEndpointUrl(configuration)
//
//        then:
//        _ * configuration.sourceHostName() >> "test"
//        _ * configuration.sourcePort() >> 443
//        1 * client.execute(_) >> response
//        1 * response.getStatusLine() >> statusLine
//        1 * statusLine.getStatusCode() >> 200
//        1 * response.getEntity() >> entity
//        1 * entity.getContentType() >> cType
//        1 * cType.getValue() >> "text/xml"
//
//        endpointUrl != null
//        endpointUrl.getUrl() == "https://test:443/services/wfs"
//    }
}