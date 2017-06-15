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
package org.codice.ddf.admin.sources.opensearch.persist

import ddf.catalog.source.FederatedSource
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.commons.SourceMessages
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class SaveOpenSearchConfigurationTest extends Specification {

    static RESULT_ARGUMENT_PATH = [SaveOpenSearchConfiguration.ID]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    static CONFIG_PATH = [BASE_PATH, SOURCE_CONFIG].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SOURCE_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    SaveOpenSearchConfiguration saveOpenSearchConfiguration

    ConfiguratorFactory configuratorFactory

    private ServiceActions serviceActions

    private ServiceReader serviceReader

    Configurator configurator

    FederatedSource federatedSource

    def federatedSources = []

    def setup() {
        configurator = Mock(Configurator)
        federatedSource = Mock(FederatedSource)
        federatedSource.getId() >> TEST_SOURCENAME
        federatedSources.add(federatedSource)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigurator() >> configurator
        }
        serviceActions = Mock(ServiceActions)
        serviceReader = Mock(ServiceReader)
        def managedServiceActions = Mock(ManagedServiceActions)
        saveOpenSearchConfiguration = new SaveOpenSearchConfiguration(configuratorFactory, serviceActions,
                managedServiceActions, serviceReader)
    }

    def 'Successfully save new OpenSearch configuration'() {
        setup:
        saveOpenSearchConfiguration.setValue(getBaseSaveConfigArgs())
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        report.result().getValue()
    }

    def 'Fail to save new OpenSearch config due to duplicate source name'() {
        setup:
        saveOpenSearchConfiguration.setValue(getBaseSaveConfigArgs())
        serviceReader.getServices(_, _) >> federatedSources

        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
        report.messages().get(0).path == SOURCE_NAME_PATH
    }

    def 'Fail to save new OpenSearch config due to failure to commit'() {
        setup:
        saveOpenSearchConfiguration.setValue(getBaseSaveConfigArgs())
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        !report.result().getValue()
        report.messages().size() == 1
        report.messages().get(0).path == CONFIG_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Successfully update existing OpenSearch configuration'() {
        setup:
        saveOpenSearchConfiguration.setValue(createUpdateFunctionArgs())
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        report.result().getValue()
    }

    def 'Fail to update config due to existing source name'() {
        setup:
        saveOpenSearchConfiguration.setValue(createUpdateFunctionArgs())
        serviceActions.read(_) >> [(ID): 'updatedName']
        serviceReader.getServices(_, _) >> [new TestSource(S_PID, 'updatedName', false), new TestSource("existingSource", TEST_SOURCENAME, false)]

        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'Fail to update config due to failure to commit'() {
        setup:
        saveOpenSearchConfiguration.setValue(createUpdateFunctionArgs())
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        !report.result().getValue()
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Fail to update config due to no existing source specified by the pid'() {
        setup:
        saveOpenSearchConfiguration.setValue(createUpdateFunctionArgs())
        serviceActions.read(S_PID) >> [:]

        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = saveOpenSearchConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 2
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 2
        report.messages()*.getPath() == [SOURCE_NAME_PATH, ENDPOINT_URL_PATH]
    }

    private def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }

    private def createUpdateFunctionArgs() {
        def args = getBaseSaveConfigArgs()
        args.put(PID, S_PID)
        return args
    }
}
