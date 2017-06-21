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
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.SourceMessages
import org.codice.ddf.admin.sources.csw.CswSourceInfoField
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import org.codice.ddf.admin.sources.fields.type.SourceConfigField
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class SaveCswConfigurationTest extends Specification {

    static TEST_OUTPUT_SCHEMA = 'testOutputSchema'

    static TEST_CSW_PROFILE = CswProfile.CSW_FEDERATION_PROFILE_SOURCE

    static CSW_PROFILE = CswProfile.DEFAULT_FIELD_NAME

    static RESULT_ARGUMENT_PATH = [SaveCswConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    static CONFIG_PATH = [BASE_PATH, CswSourceConfigurationField.DEFAULT_FIELD_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SourceConfigField.SOURCE_NAME_FIELD_NAME].flatten()

    static CSW_PROFILE_PATH = [CONFIG_PATH, CSW_PROFILE].flatten()

    SaveCswConfiguration saveCswConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ServiceReader serviceReader

    ServiceActions serviceActions

    ManagedServiceActions managedServiceActions

    FeatureActions featureActions

    Source federatedSource

    def actionArgs

    def federatedSources = []

    def setup() {
        actionArgs = createCswSaveArgs()
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory)
        serviceActions = Mock(ServiceActions)
        managedServiceActions = Mock(ManagedServiceActions)
        serviceReader = Mock(ServiceReader)
        featureActions = Mock(FeatureActions)

        federatedSource = new TestSource(S_PID, TEST_SOURCENAME, false)
        federatedSources.add(federatedSource)
        configuratorFactory.getConfigurator() >> configurator
        saveCswConfiguration = new SaveCswConfiguration(configuratorFactory, serviceActions,
                managedServiceActions, serviceReader, featureActions)
    }

    def 'Successfully save new CSW configuration'() {
        when:
        saveCswConfiguration.setValue(createCswSaveArgs())
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)
        def report = saveCswConfiguration.getValue()

        then:
        report.result() != null
        report.result().getValue()
    }

    def 'Fail to save new CSW config due to duplicate source name'() {
        when:
        saveCswConfiguration.setValue(actionArgs)
        serviceReader.getServices(_, _) >> federatedSources
        def report = saveCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'Fail to save new CSW config due to failure to commit'() {
        when:
        saveCswConfiguration.setValue(actionArgs)
        serviceReader.getServices(_, _) >> []
        def report = saveCswConfiguration.getValue()

        then:
        1 * configurator.commit(_, _) >> mockReport(false)
        1 * configurator.commit(_, _) >> mockReport(true)
        !report.result().getValue()
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Successfully update CSW configuration'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveCswConfiguration.setValue(actionArgs)
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveCswConfiguration.getValue()

        then:
        report.result() != null
        report.result().getValue()
    }

    def 'Fail CSW configuration update due to existing source name'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveCswConfiguration.setValue(actionArgs)
        serviceActions.read(_) >> [(ID): 'updatedName']
        serviceReader.getServices(_, _) >>
                [new TestSource(S_PID, 'updatedName', false),
                 new TestSource("existingSource", TEST_SOURCENAME, false)]

        when:
        def report = saveCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'Fail to update CSW config due to failure to commit'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveCswConfiguration.setValue(actionArgs)
        serviceActions.read(_) >> [(ID): TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []

        when:
        def report = saveCswConfiguration.getValue()

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
        actionArgs.put(PID, S_PID)
        saveCswConfiguration.setValue(actionArgs)
        serviceActions.read(S_PID) >> [:]

        when:
        def report = saveCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
    }

    def 'Fail when missing required fields'() {
        when:
        def report = saveCswConfiguration.getValue()

        then:
        report.result() == null
        report.messages().size() == 3
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 3
        report.messages()*.getPath() == [SOURCE_NAME_PATH, ENDPOINT_URL_PATH, CSW_PROFILE_PATH]
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }

    def createCswSaveArgs() {
        def config = new CswSourceConfigurationField()
                .outputSchema(TEST_OUTPUT_SCHEMA)
                .cswProfile(TEST_CSW_PROFILE)
                .endpointUrl('https://localhost:8993').sourceName(TEST_SOURCENAME)
        config.credentials().username(TEST_USERNAME).password(TEST_PASSWORD)
        return [(CswSourceConfigurationField.DEFAULT_FIELD_NAME): config.getValue()]
    }
}
