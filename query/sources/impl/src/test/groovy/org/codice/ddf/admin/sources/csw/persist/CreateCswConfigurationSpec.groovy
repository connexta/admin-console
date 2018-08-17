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
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.SourceMessages
import org.codice.ddf.admin.sources.fields.CswProfile
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField
import org.codice.ddf.admin.sources.fields.type.SourceConfigField
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.admin.sources.utils.SourceUtilCommons
import org.codice.ddf.admin.sources.utils.SourceValidationUtils
import org.codice.ddf.internal.admin.configurator.actions.*
import org.junit.Ignore

class CreateCswConfigurationSpec extends SourceCommonsSpec {

    static final List<Object> FUNCTION_PATH = [CreateCswConfiguration.FIELD_NAME]

    static TEST_CSW_PROFILE = CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE

    static CSW_PROFILE = CswProfile.DEFAULT_FIELD_NAME

    static RESULT_ARGUMENT_PATH = [CreateCswConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH].flatten()

    static CONFIG_PATH = [BASE_PATH, CswSourceConfigurationField.DEFAULT_FIELD_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SourceConfigField.SOURCE_NAME_FIELD_NAME].flatten()

    static CSW_PROFILE_PATH = [CONFIG_PATH, CSW_PROFILE].flatten()

    static TEST_OUTPUT_SCHEMA = 'testOutputSchema'

    CreateCswConfiguration createCswConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ServiceReader serviceReader

    ServiceActions serviceActions

    ManagedServiceActions managedServiceActions

    FeatureActions featureActions

    ConfiguratorSuite configuratorSuite

    Source federatedSource

    SourceValidationUtils sourceValidationUtils

    ServiceCommons serviceCommons

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
        configuratorSuite.serviceReader >> serviceReader
        configuratorSuite.featureActions >> featureActions
        configuratorSuite.managedServiceActions >> managedServiceActions

        serviceCommons = new ServiceCommons(configuratorSuite)
        sourceValidationUtils = new SourceValidationUtils(new SourceUtilCommons(configuratorSuite), serviceCommons)

        createCswConfiguration = new CreateCswConfiguration(sourceValidationUtils, serviceCommons)
    }

    def 'Successfully create new CSW configuration'() {
        when:
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)
        def report = createCswConfiguration.execute(createCswArgs(), FUNCTION_PATH)

        then:
        report.getResult() != null
        report.getResult().getValue()
    }

    def 'Fail to create new CSW config due to duplicate source name'() {
        when:
        serviceReader.getServices(_, _) >> federatedSources
        def report = createCswConfiguration.execute(createCswArgs(), FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == SOURCE_NAME_PATH
        report.getErrorMessages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
    }

    def 'Fail to create new CSW config due to failure to commit'() {
        when:
        serviceReader.getServices(_, _) >> []
        def report = createCswConfiguration.execute(createCswArgs(), FUNCTION_PATH)

        then:
        configurator.commit(_, _) >> mockReport(true)
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    @Ignore
    // TODO: 8/23/17 phuffer - Remove ignore when feature starts correctly
    def 'Return false when csw feature fails to start'() {
        when:
        serviceReader.getServices(_, _) >> []
        def report = createCswConfiguration.execute(createCswArgs(), FUNCTION_PATH)

        then:
        1 * configurator.commit(_, _) >> mockReport(true)
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
    }

    def 'Fail when missing required fields'() {
        when:
        def report = createCswConfiguration.execute(null, FUNCTION_PATH)

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 3
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 3
        report.getErrorMessages()*.getPath() == [SOURCE_NAME_PATH, ENDPOINT_URL_PATH, CSW_PROFILE_PATH]
    }

    def 'Returns all the possible error codes correctly'() {
        setup:
        CreateCswConfiguration createDuplicateNameConfig = new CreateCswConfiguration(sourceValidationUtils, serviceCommons)
        serviceReader.getServices(_, _) >> federatedSources

        CreateCswConfiguration createFailPersistConfig = new CreateCswConfiguration(sourceValidationUtils, serviceCommons)
        serviceReader.getServices(_, _) >> []

        when:
        def errorCodes = createCswConfiguration.getFunctionErrorCodes()
        def duplicateNameReport = createDuplicateNameConfig.execute(createCswArgs(), FUNCTION_PATH)
        def createFailPersistReport = createFailPersistConfig.execute(createCswArgs(), FUNCTION_PATH)

        then:
        errorCodes.size() == 2
        errorCodes.contains(duplicateNameReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(createFailPersistReport.getErrorMessages().get(0).getCode())
    }

    def createCswArgs() {
        def config = new CswSourceConfigurationField()
                .outputSchema(TEST_OUTPUT_SCHEMA)
                .cswProfile(TEST_CSW_PROFILE)
                .endpointUrl('https://localhost:8993').sourceName(TEST_SOURCENAME)
        config.credentials().username(TEST_USERNAME).password(TEST_PASSWORD)
        return [(CswSourceConfigurationField.DEFAULT_FIELD_NAME): config.getValue()]
    }
}
