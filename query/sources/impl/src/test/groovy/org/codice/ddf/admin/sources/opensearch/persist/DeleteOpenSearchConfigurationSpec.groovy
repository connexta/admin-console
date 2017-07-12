package org.codice.ddf.admin.sources.opensearch.persist

import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.sources.test.SourceCommonsSpec
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions

class DeleteOpenSearchConfigurationSpec extends SourceCommonsSpec {

    DeleteOpenSearchConfiguration deleteOpenSearchConfigurationFunction

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ConfiguratorSuite configuratorSuite

    static RESULT_ARGUMENT_PATH = [DeleteOpenSearchConfiguration.FIELD_NAME]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

    static PID_PATH = [BASE_PATH, PID].flatten()

    def functionArgs = [
            (PID): S_PID
    ]
    private ServiceActions serviceActions

    def setup() {
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigurator() >> configurator
        }
        serviceActions = Mock(ServiceActions)
        def managedServiceActions = Mock(ManagedServiceActions)

        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.configuratorFactory >> configuratorFactory
        configuratorSuite.serviceActions >> serviceActions
        configuratorSuite.managedServiceActions >> managedServiceActions

        deleteOpenSearchConfigurationFunction = new DeleteOpenSearchConfiguration(configuratorSuite)
    }

    def 'Successfully deleting WFS config returns true'() {
        when:
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(false)
        deleteOpenSearchConfigurationFunction.setValue(functionArgs)
        def report = deleteOpenSearchConfigurationFunction.getValue()

        then:
        report.result() != null
        report.result().getValue()
    }

    def 'Fail delete when no existing configuration with the provided pid'() {
        when:
        serviceActions.read(S_PID) >> [:]
        deleteOpenSearchConfigurationFunction.setValue(functionArgs)
        def report = deleteOpenSearchConfigurationFunction.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Error while committing delete configuration with given pid'() {
        when:
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchConfigurationFunction.setValue(functionArgs)
        def report = deleteOpenSearchConfigurationFunction.getValue()

        then:
        !report.result().getValue()
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = deleteOpenSearchConfigurationFunction.getValue()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [PID_PATH]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        DeleteOpenSearchConfiguration deleteOpenSearchNoExistingConfig = new DeleteOpenSearchConfiguration(configuratorSuite)
        serviceActions.read(S_PID) >> [:]
        deleteOpenSearchNoExistingConfig.setValue(functionArgs)

        DeleteOpenSearchConfiguration deleteOpenSearchFailPersist = new DeleteOpenSearchConfiguration(configuratorSuite)
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchFailPersist.setValue(functionArgs)

        DeleteOpenSearchConfiguration deleteOpenSearchMissingField = new DeleteOpenSearchConfiguration(configuratorSuite)

        when:
        def errorCodes = deleteOpenSearchConfigurationFunction.getFunctionErrorCodes()
        def noExistingConfigReport = deleteOpenSearchNoExistingConfig.getValue()
        def failedPersistReport = deleteOpenSearchFailPersist.getValue()
        def missingFieldReport = deleteOpenSearchMissingField.getValue()

        then:
        errorCodes.size() == 3
        errorCodes.contains(noExistingConfigReport.messages().get(0).getCode())
        errorCodes.contains(failedPersistReport.messages().get(0).getCode())
        errorCodes.contains(missingFieldReport.messages().get(0).getCode())
    }
}
