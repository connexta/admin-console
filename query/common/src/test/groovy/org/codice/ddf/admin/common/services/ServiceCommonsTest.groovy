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
package org.codice.ddf.admin.common.services

import org.codice.ddf.admin.common.fields.common.PidField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import spock.lang.Specification

class ServiceCommonsTest extends Specification {

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    Configurator configurator

    def setup() {
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory)
        configReader = Mock(ConfigReader)
        configuratorFactory.getConfigurator() >> configurator
        configuratorFactory.getConfigReader() >> configReader
    }

    def 'Create managed service success'() {
        setup:
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = ServiceCommons.createManagedService([:], '', configuratorFactory)

        then:
        report.messages().size() == 0
    }

    def 'Configurator fails to commit when creating managed service'() {
        setup:
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = ServiceCommons.createManagedService([:], '', configuratorFactory)

        then:
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.FAILED_PERSIST
        report.messages()[0].getPath() == []
    }

    def 'Update service success'() {
        setup:
        configReader.getConfig(_) >> ['config':'exists']
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = ServiceCommons.updateService(createTestField('testValue'), [:], configuratorFactory)

        then:
        report.messages().size() == 0
    }

    def 'Configurator fails to commit when updating service'() {
        setup:
        configReader.getConfig(_) >> ['config':'exists']
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = ServiceCommons.updateService(createTestField('testValue'), [:], configuratorFactory)

        then:
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.FAILED_UPDATE_ERROR
        report.messages()[0].getPath() == []
    }

    def 'Delete service success'() {
        setup:
        configReader.getConfig(_) >> ['config':'exists']
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = ServiceCommons.deleteService(createTestField('testValue'), configuratorFactory)

        then:
        report.messages().size() == 0
    }

    def 'Configurator fails to commit when deleting service'() {
        setup:
        configReader.getConfig(_) >> ['config':'exists']
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = ServiceCommons.deleteService(createTestField('testValue'), configuratorFactory)

        then:
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.FAILED_DELETE_ERROR
        report.messages()[0].getPath() == []
    }

    def 'Configuration exists'() {
        setup:
        configReader.getConfig(_) >> ['config':'exists']

        when:
        def report = ServiceCommons.serviceConfigurationExists(createTestField('testValue'), configuratorFactory)

        then:
        report.messages().size() == 0
    }

    def 'Configuration does not exist'() {
        setup:
        configReader.getConfig(_) >> [:]

        when:
        def report = ServiceCommons.serviceConfigurationExists(createTestField('testValue'), configuratorFactory)

        then:
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.NO_EXISTING_CONFIG
        report.messages()[0].getPath() == []
    }

    def 'Service config builder does not have null values'() {
        when:
        def props = new ServiceCommons.ServicePropertyBuilder().putPropertyIfNotNull('key', createTestField(null)).build()

        then:
        props.isEmpty()
    }

    def mockReport(boolean containsErrors) {
        def opReport = Mock(OperationReport)
        opReport.containsFailedResults() >> containsErrors
        return opReport
    }

    def createTestField(String value) {
        def field = new PidField()
        field.setValue(value)
        return field
    }
}
