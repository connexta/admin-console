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
package org.codice.ddf.admin.sources.csw.persist

import ddf.catalog.source.Source
import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.SourceMessages
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import org.codice.ddf.admin.sources.fields.type.SourceConfigField
import org.codice.ddf.admin.sources.services.CswServiceProperties
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader

class UpdateCswConfigurationSpec extends SourceCommonsSpec {

    static TEST_OUTPUT_SCHEMA = 'testOutputSchema'

    static TEST_URL = 'https://localhost:8993'

    static TEST_CSW_PROFILE = CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE

    static CSW_PROFILE = CswProfile.DEFAULT_FIELD_NAME

    static RESULT_ARGUMENT_PATH = [UpdateCswConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    static CONFIG_PATH = [BASE_PATH, CswSourceConfigurationField.DEFAULT_FIELD_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static SERVICE_PID_PATH = [CONFIG_PATH, PID].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SourceConfigField.SOURCE_NAME_FIELD_NAME].flatten()

    static CSW_PROFILE_PATH = [CONFIG_PATH, CSW_PROFILE].flatten()

    UpdateCswConfiguration updateCswConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ServiceReader serviceReader

    ServiceActions serviceActions

    ManagedServiceActions managedServiceActions

    FeatureActions featureActions

    ConfiguratorSuite configuratorSuite

    Source federatedSource

    def federatedSources = []

    def setup() {
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        managedServiceActions = Mock(ManagedServiceActions)
        serviceReader = Mock(ServiceReader)
        featureActions = Mock(FeatureActions)

        federatedSource = new TestSource(S_PID, TEST_SOURCENAME, false)
        federatedSources.add(federatedSource)
        configuratorFactory.getConfigurator() >> configurator

        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.managedServiceActions >> managedServiceActions
        configuratorSuite.serviceReader >> serviceReader
        configuratorSuite.featureActions >> featureActions
        updateCswConfiguration = new UpdateCswConfiguration(configuratorSuite)
    }

    def 'Successfully update CSW configuration'() {
        setup:
        updateCswConfiguration.setValue(createCswUpdateArgs(TEST_PASSWORD))
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = updateCswConfiguration.getValue()

        then:
        report.result() != null
        report.result().getValue()
    }

    def 'Fail CSW configuration update due to existing source name'() {
        setup:
        updateCswConfiguration.setValue(createCswUpdateArgs(TEST_PASSWORD))
        serviceActions.read(_) >> [(ID): 'updatedName']
        serviceReader.getServices(_, _) >>
                [new TestSource(S_PID, 'updatedName', false),
                 new TestSource("existingSource", TEST_SOURCENAME, false)]

        when:
        def report = updateCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'Fail to update CSW config due to failure to commit'() {
        setup:
        updateCswConfiguration.setValue(createCswUpdateArgs(TEST_PASSWORD))
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []

        when:
        def report = updateCswConfiguration.getValue()

        then:
        1 * configurator.commit(_, _) >> mockReport(false)
        1 * configurator.commit(_, _) >> mockReport(true)
        !report.result().getValue()
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Fail to update CSW Configuration due to no existing source config'() {
        setup:
        updateCswConfiguration.setValue(createCswUpdateArgs(TEST_PASSWORD))
        serviceActions.read(S_PID) >> [:]

        when:
        def report = updateCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
    }

    def 'Return false when csw feature fails to start'() {
        setup:
        updateCswConfiguration.setValue(createCswUpdateArgs(TEST_PASSWORD))
        serviceReader.getServices(_, _) >> []
        serviceActions.read(_ as String) >> [(ID): TEST_SOURCENAME]

        when:
        def report = updateCswConfiguration.getValue()

        then:
        1 * configurator.commit(_, _) >> mockReport(true)
        !report.result().getValue()
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Updating with flag password sends service properties without password'() {
        setup:
        updateCswConfiguration.setValue(createCswUpdateArgs(FLAG_PASSWORD))
        serviceActions.read(_ as String) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        def expectedUpdateConfig = CswServiceProperties.cswConfigToServiceProps(createCswSourceConfig(FLAG_PASSWORD))

        when:
        updateCswConfiguration.getValue()

        then:
        1 * serviceActions.build(S_PID, expectedUpdateConfig, true)
    }

    def 'Updating with new password sends service properties with password'() {
        setup:
        updateCswConfiguration.setValue(createCswUpdateArgs('notFlagPassword'))
        serviceActions.read(_ as String) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        def expectedUpdateConfig = CswServiceProperties.cswConfigToServiceProps(createCswSourceConfig('notFlagPassword'))

        when:
        updateCswConfiguration.getValue()

        then:
        1 * serviceActions.build(S_PID, expectedUpdateConfig, true)
    }

    def 'Fail when missing required fields'() {
        when:
        def report = updateCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 4
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 4
        report.messages()*.getPath() == [SERVICE_PID_PATH, SOURCE_NAME_PATH, ENDPOINT_URL_PATH, CSW_PROFILE_PATH]
    }

    def 'Returns all the possible error codes correctly'(){
        when:
        def errorCodes = updateCswConfiguration.getFunctionErrorCodes()

        then:
        errorCodes.size() == 3
        errorCodes.contains(DefaultMessages.NO_EXISTING_CONFIG)
        errorCodes.contains(DefaultMessages.FAILED_PERSIST)
        errorCodes.contains(SourceMessages.DUPLICATE_SOURCE_NAME)
    }

    def createCswUpdateArgs(String password) {
        return [(CswSourceConfigurationField.DEFAULT_FIELD_NAME): createCswSourceConfig(password).getValue()]
    }

    def createCswSourceConfig(String password) {
        def config = new CswSourceConfigurationField()
                .outputSchema(TEST_OUTPUT_SCHEMA)
                .cswProfile(TEST_CSW_PROFILE)
        config.endpointUrl(TEST_URL).sourceName(TEST_SOURCENAME)
                .pid(S_PID)
        config.credentials().username(TEST_USERNAME).password(password)
        return config
    }
}
