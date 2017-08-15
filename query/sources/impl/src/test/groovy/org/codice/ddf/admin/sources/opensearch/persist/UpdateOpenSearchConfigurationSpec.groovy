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
import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.SourceMessages
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField
import org.codice.ddf.admin.sources.fields.type.SourceConfigField
import org.codice.ddf.admin.sources.services.OpenSearchServiceProperties
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader

class UpdateOpenSearchConfigurationSpec extends SourceCommonsSpec {

    static RESULT_ARGUMENT_PATH = [UpdateOpenSearchConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    static CONFIG_PATH = [BASE_PATH, OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SourceConfigField.SOURCE_NAME_FIELD_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static PID_PATH = [CONFIG_PATH, PID].flatten()

    UpdateOpenSearchConfiguration updateOpenSearchConfiguration

    ConfiguratorFactory configuratorFactory

    ServiceActions serviceActions

    ServiceReader serviceReader

    FeatureActions featureActions

    ManagedServiceActions managedServiceActions

    Configurator configurator

    FederatedSource federatedSource

    def federatedSources = []

    def setup() {
        configurator = Mock(Configurator)
        serviceActions = Mock(ServiceActions)
        serviceReader = Mock(ServiceReader)
        managedServiceActions = Mock(ManagedServiceActions)
        featureActions = Mock(FeatureActions)

        federatedSource = Mock(FederatedSource)
        federatedSource.getId() >> TEST_SOURCENAME
        federatedSources.add(federatedSource)
        configuratorFactory = Mock(ConfiguratorFactory)
        configuratorFactory.getConfigurator() >> configurator

        def configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.serviceReader >> serviceReader
        configuratorSuite.managedServiceActions >> managedServiceActions
        configuratorSuite.featureActions >> featureActions

        updateOpenSearchConfiguration = new UpdateOpenSearchConfiguration(configuratorSuite)
    }

    def 'Successfully update existing OpenSearch configuration'() {
        setup:
        updateOpenSearchConfiguration.setArguments(createUpdateFunctionArgs(FLAG_PASSWORD))
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = updateOpenSearchConfiguration.execute()

        then:
        report.getResult().getValue()
    }

    def 'Fail to update config due to existing source name'() {
        setup:
        updateOpenSearchConfiguration.setArguments(createUpdateFunctionArgs(FLAG_PASSWORD))
        serviceActions.read(_) >> [(ID): 'updatedName']
        serviceReader.getServices(_, _) >> [new TestSource(S_PID, 'updatedName', false), new TestSource("existingSource", TEST_SOURCENAME, false)]

        when:
        def report = updateOpenSearchConfiguration.execute()

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == SOURCE_NAME_PATH
        report.getErrorMessages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'Fail to update config due to failure to commit'() {
        setup:
        updateOpenSearchConfiguration.setArguments(createUpdateFunctionArgs(FLAG_PASSWORD))
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = updateOpenSearchConfiguration.execute()

        then:
        1 * configurator.commit(_, _) >> mockReport(false)
        1 * configurator.commit(_, _) >> mockReport(true)
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Fail to update config due to no existing source specified by the pid'() {
        setup:
        updateOpenSearchConfiguration.setArguments(createUpdateFunctionArgs(FLAG_PASSWORD))
        serviceActions.read(S_PID) >> [:]

        when:
        def report = updateOpenSearchConfiguration.execute()

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Return false when opensearch feature fails to start'() {
        setup:
        updateOpenSearchConfiguration.setArguments(createUpdateFunctionArgs(FLAG_PASSWORD))
        serviceReader.getServices(_, _) >> []
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]

        when:
        def report = updateOpenSearchConfiguration.execute()

        then:
        1 * configurator.commit(_, _) >> mockReport(true)
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Updating with flag password sends service properties without password'() {
        setup:
        updateOpenSearchConfiguration.setArguments(createUpdateFunctionArgs(FLAG_PASSWORD))
        serviceActions.read(_ as String) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        def expectedUpdateConfig = OpenSearchServiceProperties.openSearchConfigToServiceProps(createOpenSearchSourceConfig(FLAG_PASSWORD))

        when:
        updateOpenSearchConfiguration.execute()

        then:
        1 * serviceActions.build(S_PID, expectedUpdateConfig, true)
    }

    def 'Updating with new password sends service properties with password'() {
        setup:
        updateOpenSearchConfiguration.setArguments(createUpdateFunctionArgs('notFlagPassword'))
        serviceActions.read(_ as String) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        def expectedUpdateConfig = OpenSearchServiceProperties.openSearchConfigToServiceProps(createOpenSearchSourceConfig('notFlagPassword'))

        when:
        updateOpenSearchConfiguration.execute()

        then:
        1 * serviceActions.build(S_PID, expectedUpdateConfig, true)
    }

    def 'Fail when missing required fields'() {
        when:
        def report = updateOpenSearchConfiguration.execute()

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 3
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 3
        report.getErrorMessages()*.getPath() == [PID_PATH, SOURCE_NAME_PATH, ENDPOINT_URL_PATH]
    }

    def 'Returns all the possible error codes correctly'(){
        when:
        def errorCodes = updateOpenSearchConfiguration.getFunctionErrorCodes()

        then:
        errorCodes.size() == 3
        errorCodes.contains(DefaultMessages.NO_EXISTING_CONFIG)
        errorCodes.contains(DefaultMessages.FAILED_PERSIST)
        errorCodes.contains(SourceMessages.DUPLICATE_SOURCE_NAME)
    }

    def createUpdateFunctionArgs(String password) {
        return [(OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME): createOpenSearchSourceConfig(password).getValue()]
    }

    def createOpenSearchSourceConfig(String password) {
        def config = new OpenSearchSourceConfigurationField()
        config.endpointUrl('https://localhost:8993').sourceName(TEST_SOURCENAME)
                .pid(S_PID).credentials().username(TEST_USERNAME).password(password)
        return config
    }
}
