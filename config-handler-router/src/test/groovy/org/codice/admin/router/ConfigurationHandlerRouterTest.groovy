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
package org.codice.admin.router

import org.codice.ddf.admin.api.config.Configuration
import org.codice.ddf.admin.api.config.ConfigurationType
import org.codice.ddf.admin.api.config.sources.SourceConfiguration
import org.codice.ddf.admin.api.config.sources.WfsSourceConfiguration
import org.codice.ddf.admin.api.handler.ConfigurationHandler
import org.codice.ddf.admin.api.handler.ConfigurationMessage
import org.codice.ddf.admin.api.handler.report.CapabilitiesReport
import org.codice.ddf.admin.api.handler.report.ProbeReport
import org.codice.ddf.admin.api.handler.report.Report
import spark.Request
import spark.Response
import spock.lang.Specification

class ConfigurationHandlerRouterTest extends Specification {

    static final CONFIGURATION_TYPE = "configurationType"

    static final MATCHING_ID = "myId"

    static final NON_MATCHING_ID = "notMyId"

    static final METHOD_ID = "someId"

    ConfigurationHandlerRouter configurationHandlerRouter

    Response response

    ConfigurationHandler configurationHandler

    Request request

    def setup() {
        configurationHandlerRouter = new ConfigurationHandlerRouter()
    }

    def 'test test(Request, Response) config handler success'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        def report = mockReport(false)

        when:
        configurationHandlerRouter.test(request, response)


        then:
        1 * configurationHandler.test(METHOD_ID, _ as Configuration) >> report
        0 * response.status(400)
    }

    def 'test test(Request, Response) with no available config handlers'() {
        setup:
        response = Mock(Response)
        request = mockRequest(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        def report = configurationHandlerRouter.test(request, response)


        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.test(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test test(Request, Response) with no matching config handler'() {
        setup:
        prepareMocks(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        def report = configurationHandlerRouter.test(request, response)

        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.test(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test test(Request, Response) config handler but fail test'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        def report = mockReport(true)

        when:
        configurationHandlerRouter.test(request, response)


        then:
        1 * configurationHandler.test(METHOD_ID, _ as Configuration) >> report
        1 * response.status(400)
    }

    def 'test test(Request, Response) with null configHandlerId request param'() {
        setup:
        prepareMocks(null, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        def report = configurationHandlerRouter.test(request, response)


        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(null)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.test(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    /**
     * This test does not indicate the behavior of the method throwing the throwable. Some exceptions
     * during runtime are not handled by the Spark exception handling since Spark only handles {@link Exception}s
     * and not {@link Throwable}s.
     */
    def 'test test(Request, Response) unexpected throwable'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        configurationHandlerRouter.test(request, response)

        then:
        1 * configurationHandler.test(METHOD_ID, _ as Configuration) >> new Throwable()
        thrown(ConfigurationHandlerRouterException)
    }

    def 'test persist(Request, Response) config handler success'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PERSIST_ID)

        def report = mockReport(false)

        when:
        configurationHandlerRouter.persist(request, response)


        then:
        1 * configurationHandler.persist(METHOD_ID, _ as Configuration) >> report
        0 * response.status(400)
    }

    def 'test persist(Request, Response) with no available config handlers'() {
        setup:
        response = Mock(Response)
        request = mockRequest(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PERSIST_ID)

        when:
        def report = configurationHandlerRouter.persist(request, response)


        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.test(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test persist(Request, Response) with no matching config handler'() {
        setup:
        prepareMocks(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PERSIST_ID)

        when:
        def report = configurationHandlerRouter.persist(request, response)

        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.persist(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test persist(Request, Response) with null configHandlerId request param'() {
        setup:
        prepareMocks(null, METHOD_ID, ConfigurationHandlerRouter.PERSIST_ID)

        when:
        def report = configurationHandlerRouter.persist(request, response)


        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(null)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.test(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test persist(Request, Response) config handler but fail persist'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PERSIST_ID)

        def report = mockReport(true)

        when:
        configurationHandlerRouter.persist(request, response)

        then:
        1 * configurationHandler.persist(METHOD_ID, _ as Configuration) >> report
        1 * response.status(400)
    }

    /**
     * This test does not indicate the behavior of the method throwing the throwable. Some exceptions
     * during runtime are not handled by the Spark exception handling since Spark only handles {@link Exception}s
     * and not {@link Throwable}s.
     */
    def 'test persist(Request, Response) unexpected throwable'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PERSIST_ID)

        when:
        configurationHandlerRouter.persist(request, response)

        then:
        1 * configurationHandler.persist(METHOD_ID, _ as Configuration) >> new Throwable()
        thrown(ConfigurationHandlerRouterException)
    }

    def 'test probe(Request, Response) config handler success'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PROBE_ID)

        def report = Mock(ProbeReport) {
            containsUnsuccessfulMessages() >> false
        }

        when:
        configurationHandlerRouter.probe(request, response)


        then:
        1 * configurationHandler.probe(METHOD_ID, _ as Configuration) >> report
        0 * response.status(400)
    }

    def 'test probe(Request, Response) with no available config handlers'() {
        setup:
        response = Mock(Response)
        request = mockRequest(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PROBE_ID)

        when:
        def report = configurationHandlerRouter.probe(request, response)


        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.test(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test probe(Request, Response) with no matching config handler'() {
        setup:
        prepareMocks(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PROBE_ID)

        when:
        def report = configurationHandlerRouter.probe(request, response)

        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.probe(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test probe(Request, Response) with null configHandlerId request param'() {
        setup:
        prepareMocks(null, METHOD_ID, ConfigurationHandlerRouter.PROBE_ID)

        when:
        def report = configurationHandlerRouter.probe(request, response)


        then:
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(null)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
        0 * configurationHandler.test(_ as String, _ as Configuration)
        1 * response.status(400)
    }

    def 'test probe(Request, Response) config handler but fail persist'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PROBE_ID)

        def report = Mock(ProbeReport) {
            containsUnsuccessfulMessages() >> true
        }

        when:
        configurationHandlerRouter.probe(request, response)


        then:
        1 * configurationHandler.probe(METHOD_ID, _ as Configuration) >> report
        1 * response.status(400)
    }

    /**
     * This test does not indicate the behavior of the method throwing the throwable. Some exceptions
     * during runtime are not handled by the Spark exception handling since Spark only handles {@link Exception}s
     * and not {@link Throwable}s.
     */
    def 'test probe(Request, Response) unexpected throwable'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.PROBE_ID)

        when:
        configurationHandlerRouter.probe(request, response)

        then:
        1 * configurationHandler.probe(METHOD_ID, _ as Configuration) >> new Throwable()
        thrown(ConfigurationHandlerRouterException)
    }

    def 'test getCapabilities()'() {
        setup:
        prepareMocks(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        def reports = (List<CapabilitiesReport>)configurationHandlerRouter.getCapabilities()

        then:
        reports.size() == 1
        reports.get(0).getConfigurationType() == CONFIGURATION_TYPE
    }

    def 'test configCapabilties(Request, Response) success'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        configurationHandlerRouter.configCapabilities(request, response)

        then:
        1 * configurationHandler.getCapabilities()
        configurationHandler.getCapabilities().configurationType == CONFIGURATION_TYPE
    }

    def 'test configCapabilties(Request, Response) with no matching config handler'() {
        setup:
        prepareMocks(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        def report = (Report) configurationHandlerRouter.configCapabilities(request, response)

        then:
        1 * response.status(400)
        0 * configurationHandler.getCapabilities()
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
    }

    def 'test configurations(Request, Response) success'() {
        setup:
        prepareMocks(MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        configurationHandlerRouter.configurations(request, response)

        then:
        1 * configurationHandler.getConfigurations()
        configurationHandler.getCapabilities().configurationType == CONFIGURATION_TYPE
    }

    def 'test configurations(Request, Response) with no matching config handler'() {
        setup:
        prepareMocks(NON_MATCHING_ID, METHOD_ID, ConfigurationHandlerRouter.TEST_ID)

        when:
        def report = (Report) configurationHandlerRouter.configurations(request, response)

        then:
        1 * response.status(400)
        0 * configurationHandler.getConfigurations()
        report.messages().size() == 1
        report.messages().get(0).message() == getFailureMessage(NON_MATCHING_ID)
        report.messages().get(0).configFieldId() == ConfigurationHandlerRouter.CONFIGURATION_TYPE_FIELD
        report.messages().get(0).type() == ConfigurationMessage.MessageType.FAILURE
        report.messages().get(0).subtype() == ConfigurationMessage.INVALID_FIELD
        report.containsFailureMessages()
    }

    def prepareMocks(String configHandlerId, String methodId, String paramId) {
        response = Mock(Response)
        configurationHandler = mockConfigurationHandler()
        request = mockRequest(configHandlerId, methodId, paramId)

        configurationHandlerRouter.setConfigurationHandlers([configurationHandler])
    }

    def mockConfigurationHandler() {
        return Mock(ConfigurationHandler) {
            getConfigurationHandlerId() >> MATCHING_ID
            getConfigurationType() >> Mock(ConfigurationType) {
                configClass() >> WfsSourceConfiguration
                configTypeName() >> "sources"
            }
            getCapabilities() >> Mock(CapabilitiesReport) {
                getConfigurationType() >> CONFIGURATION_TYPE
            }
        }
    }

    def mockRequest(String configHandlerId, String methodId, String paramId) {
        return Mock(Request) {
            def baseConfiguration = new SourceConfiguration()
            baseConfiguration.configurationType

            def configuration = new WfsSourceConfiguration()
            configuration.configurationType

            params(ConfigurationHandlerRouter.CONFIG_HANDLER_ID) >> configHandlerId
            params(paramId) >> methodId
            body() >> "{\"configurationType\":\"sources\",\"sourceHostName\":\"localhost\",\"sourcePort\":8993}"
        }
    }

    def mockReport(boolean containsUnsuccessfulMessage) {
        return Mock(Report) {
            containsUnsuccessfulMessages() >> containsUnsuccessfulMessage
        }
    }

    def getFailureMessage(String configHandlerId) {
        return String.format(ConfigurationHandlerRouter.NO_CONFIG_ID_MSG, configHandlerId)
    }

    def mockBadTestIdReport() {
        return mock(Report) >> {

        }
    }
}