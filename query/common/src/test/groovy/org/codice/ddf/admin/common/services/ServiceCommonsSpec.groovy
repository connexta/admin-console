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

import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import org.codice.ddf.admin.common.fields.common.PidField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import spock.lang.Specification

class ServiceCommonsSpec extends Specification {
    ConfiguratorSuite configuratorSuite

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ManagedServiceActions managedServiceActions

    ServiceActions serviceActions

    ServiceReader serviceReader

    ServiceCommons serviceCommons

    def setup() {
        managedServiceActions = Mock(ManagedServiceActions)
        serviceActions = Mock(ServiceActions)
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceReader = Mock(ServiceReader)
        configuratorFactory.getConfigurator() >> configurator

        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.getConfiguratorFactory() >> configuratorFactory
        configuratorSuite.getManagedServiceActions() >> managedServiceActions
        configuratorSuite.getServiceActions() >> serviceActions
        configuratorSuite.getServiceReader() >> serviceReader

        serviceCommons = new ServiceCommons(configuratorSuite)
    }

    def 'Create managed service success'() {
        setup:
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = serviceCommons.createManagedService([:], '')

        then:
        report.getErrorMessages().size() == 0
    }

    def 'Configurator fails to commit when creating managed service'() {
        setup:
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = serviceCommons.createManagedService([:], '')

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages()[0].getPath() == []
    }

    def 'Update service success'() {
        setup:
        serviceActions.read(_) >> ['config': 'exists']
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = serviceCommons.updateService(createTestField('testValue'), [:])

        then:
        report.getErrorMessages().size() == 0
    }

    def 'Configurator fails to commit when updating service'() {
        setup:
        serviceActions.read(_) >> ['config': 'exists']
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = serviceCommons.updateService(createTestField('testValue'), [:])

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages()[0].getPath() == []
    }

    def 'Delete service success'() {
        setup:
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = serviceCommons.deleteService(createTestField('testValue'))

        then:
        report.getErrorMessages().size() == 0
    }

    def 'Configurator fails to commit when deleting service'() {
        setup:
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = serviceCommons.deleteService(createTestField('testValue'))

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages()[0].getPath() == []
    }

    def 'Configuration exists'() {
        setup:
        serviceActions.read(_) >> ['config': 'exists']

        when:
        def report = serviceCommons.serviceConfigurationExists(createTestField('testValue'))

        then:
        report.getErrorMessages().size() == 0
    }

    def 'Configuration does not exist'() {
        setup:
        serviceActions.read(_) >> [:]

        when:
        def report = serviceCommons.serviceConfigurationExists(createTestField('testValue'))

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.NO_EXISTING_CONFIG
        report.getErrorMessages()[0].getPath() == []
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
