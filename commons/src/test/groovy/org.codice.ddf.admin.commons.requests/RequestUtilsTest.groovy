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
package org.codice.ddf.admin.commons.requests

import org.apache.http.HttpEntity
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.codice.ddf.admin.api.handler.report.Report
import shaded.org.apache.commons.io.IOUtils
import spock.lang.Specification

import javax.net.ssl.SSLPeerUnverifiedException

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.MessageType.*

class RequestUtilsTest extends Specification {

    def utils
    def client = Mock(CloseableHttpClient)
    def request = Mock(HttpGet)
    def response = Mock(CloseableHttpResponse)
    def statusLine = Mock(StatusLine)
    def entity = Mock(HttpEntity)

    def setup() {
        utils = Spy(RequestUtils)
    }
    def 'test sendHttpRequest good response'() {
        setup:
        def responseMap = [statusCode: 200,
                              contentType: "test/test",
                              content: "testContent"]

        when:
        def report = utils.sendHttpRequest(request)

        then:
        utils.getHttpClient(false) >> client
        client.execute(request) >> response
        response.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        utils.responseToMap(_) >> responseMap

        report.probeResults() == responseMap
        !report.containsFailureMessages()
        report.messages().find {it.type() == SUCCESS && it.subtype() == RequestUtils.EXECUTED_REQUEST}
    }

    def 'test sendHttpRequest bad response'() {
        when:
        def report = utils.sendHttpRequest(request)

        then:
        utils.getHttpClient(false) >> client
        client.execute(request) >> response
        response.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 400

        report.messages().find {it.type() == FAILURE && it.subtype() == RequestUtils.CANNOT_CONNECT}
    }

    def 'test sendHttpRequest SSL cert error'() {
        when:
        def report = utils.sendHttpRequest(request)

        then:
        utils.getHttpClient(false) >> client
        client.execute(request) >> {throw new SSLPeerUnverifiedException("Danger!")}

        report.messages().find {it.type() == FAILURE && it.subtype() == RequestUtils.CERT_ERROR}
    }

    def 'test sendHttpRequest untrusted CA success case'() {
        setup:
        def responseMap = [statusCode: 200,
                           contentType: "test/test",
                           content: "testContent"]

        when:
        def report = utils.sendHttpRequest(request)

        then:
        utils.getHttpClient(false) >> {throw new IOException()}

        then:
        utils.getHttpClient(true) >> client
        client.execute(request) >> response
        response.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        utils.responseToMap(_) >> responseMap

        report.probeResults() == responseMap
        !report.containsFailureMessages()
        report.messages().find {it.type() == WARNING && it.subtype() == RequestUtils.UNTRUSTED_CA}
        report.messages().find {it.type() == SUCCESS && it.subtype() == RequestUtils.EXECUTED_REQUEST}
    }

    def 'test sendHttpRequest untrusted CA fail case'() {
        when:
        def report = utils.sendHttpRequest(request)

        then:
        utils.getHttpClient(false) >> {throw new IOException()}

        then:
        utils.getHttpClient(true) >> client
        client.execute(request) >> response
        response.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 400
        report.messages().find {it.type() == FAILURE && it.subtype() == RequestUtils.CANNOT_CONNECT}
    }

    def 'test sendHttpRequest client cannot connect'() {
        when:
        def report = utils.sendHttpRequest(request)

        then:
        utils.getHttpClient(true) >> {throw new IOException()}
        then:
        utils.getHttpClient(false) >> {throw new IOException()}
        report.messages().find {it.type() == FAILURE && it.subtype() == RequestUtils.CANNOT_CONNECT}
    }

    def 'test responseToMap'() {
        when:
        def map = utils.responseToMap(response)

        then:
        response.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 123
        response.getEntity() >> entity
        entity.getContentType() >> null
        entity.getContent() >> IOUtils.toInputStream("Test content here")
        map.any { k,v -> k == RequestUtils.STATUS_CODE && v == 123 }
        map.any { k,v -> k == RequestUtils.CONTENT_TYPE && v == "NONE" }
        map.any { k,v -> k == RequestUtils.CONTENT && v == "Test content here" }
    }

    def 'test sendPostRequest fails if URL isn\'t reachable'() {
        setup:
        def failedReport = Mock(Report)
        failedReport.containsFailureMessages() >> true

        when:
        utils.sendPostRequest("testUrl", null, null, null, "{content: test}")

        then:
        utils.endpointIsReachable("testUrl") >> failedReport
        0 * utils.sendHttpRequest(_)
    }

    def 'test sendGetRequest fails if URL isn\'t reachable'() {
       setup:
       def failedReport = Mock(Report)
        failedReport.containsFailureMessages() >> true

        when:
        utils.sendGetRequest("testUrl", null, null)

        then:
        utils.endpointIsReachable("testUrl") >> failedReport
        0 * utils.sendHttpRequest(_)
    }

    def 'test sendGetRequest with good URL'() {
        setup:
        def goodReport = Mock(Report)
        goodReport.containsFailureMessages() >> false

        when:
        utils.sendGetRequest("testUrl", null, null)

        then:
        utils.endpointIsReachable("testUrl") >> goodReport
        1 * utils.sendHttpRequest(_) >> null
    }

    def 'test sendPostRequest with good URL'() {
        setup:
        def goodReport = Mock(Report)
        goodReport.containsFailureMessages() >> false

        when:
        utils.sendPostRequest("testUrl", null, null, "test/test", "Here is some content")

        then:
        utils.endpointIsReachable("testUrl") >> goodReport
        1 * utils.sendHttpRequest(_)
    }
}