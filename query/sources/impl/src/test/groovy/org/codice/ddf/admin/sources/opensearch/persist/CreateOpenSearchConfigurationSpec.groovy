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
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.SourceMessages
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField
import org.codice.ddf.admin.sources.fields.type.SourceConfigField
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import org.junit.Ignore

class CreateOpenSearchConfigurationSpec extends SourceCommonsSpec {

    static final List<Object> FUNCTION_PATH = [CreateOpenSearchConfiguration.FIELD_NAME]

    static RESULT_ARGUMENT_PATH = [CreateOpenSearchConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH].flatten()

    static CONFIG_PATH = [BASE_PATH, OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SourceConfigField.SOURCE_NAME_FIELD_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    CreateOpenSearchConfiguration createOpenSearchConfiguration

    ConfiguratorFactory configuratorFactory

    ServiceActions serviceActions

    ServiceReader serviceReader

    FeatureActions featureActions

    ManagedServiceActions managedServiceActions

    Configurator configurator

    ConfiguratorSuite configuratorSuite

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

        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.serviceReader >> serviceReader
        configuratorSuite.managedServiceActions >> managedServiceActions
        configuratorSuite.featureActions >> featureActions

        createOpenSearchConfiguration = new CreateOpenSearchConfiguration(configuratorSuite)
    }

    def 'Successfully create new OpenSearch configuration'() {
        setup:
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = createOpenSearchConfiguration.execute(createFunctionArgs(), FUNCTION_PATH)

        then:
        report.getResult().getValue()
    }

    def 'Fail to create new OpenSearch config due to duplicate source name'() {
        setup:
        serviceReader.getServices(_, _) >> federatedSources

        when:
        def report = createOpenSearchConfiguration.execute(createFunctionArgs(), FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
        report.getErrorMessages().get(0).path == SOURCE_NAME_PATH
    }

    def 'Fail to create new OpenSearch config due to failure to commit'() {
        setup:
        serviceReader.getServices(_, _) >> []

        when:
        def report = createOpenSearchConfiguration.execute(createFunctionArgs(), FUNCTION_PATH)

        then:
        configurator.commit(_, _) >> mockReport(true)
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    @Ignore
    // TODO: 8/23/17 phuffer - Remove ignore when feature starts correctly
    def 'Return false when opensearch feature fails to start'() {
        when:
        serviceReader.getServices(_, _) >> []
        def report = createOpenSearchConfiguration.execute(createFunctionArgs(), FUNCTION_PATH)

        then:
        1 * configurator.commit(_, _) >> mockReport(true)
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Fail when missing required fields'() {
        when:
        def report = createOpenSearchConfiguration.execute(null, FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 2
        report.getErrorMessages()*.getPath() == [SOURCE_NAME_PATH, ENDPOINT_URL_PATH]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        CreateOpenSearchConfiguration createDuplicateNameConfig = new CreateOpenSearchConfiguration(configuratorSuite)
        serviceReader.getServices(_, _) >> federatedSources

        CreateOpenSearchConfiguration createFailPersistConfig = new CreateOpenSearchConfiguration(configuratorSuite)
        serviceReader.getServices(_, _) >> []

        when:
        def errorCodes = createOpenSearchConfiguration.getFunctionErrorCodes()
        def duplicateNameReport = createDuplicateNameConfig.execute(createFunctionArgs(), FUNCTION_PATH)
        def createFailPersistReport = createFailPersistConfig.execute(createFunctionArgs(), FUNCTION_PATH)

        then:
        errorCodes.size() == 2
        errorCodes.contains(duplicateNameReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(createFailPersistReport.getErrorMessages().get(0).getCode())
    }

    def createFunctionArgs() {
        def config = new OpenSearchSourceConfigurationField().endpointUrl('https://localhost:8993').sourceName(TEST_SOURCENAME)
        config.credentials().username(TEST_USERNAME).password(TEST_PASSWORD)
        return [(OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME): config.getValue()]
    }
}
