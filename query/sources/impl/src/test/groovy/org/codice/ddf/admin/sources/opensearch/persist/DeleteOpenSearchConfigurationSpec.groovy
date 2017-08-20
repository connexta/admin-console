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
        deleteOpenSearchConfigurationFunction.setArguments(functionArgs)
        def report = deleteOpenSearchConfigurationFunction.execute()

        then:
        report.getResult() != null
        report.getResult().getValue()
    }

    def 'Fail delete when no existing configuration with the provided pid'() {
        when:
        serviceActions.read(S_PID) >> [:]
        deleteOpenSearchConfigurationFunction.setArguments(functionArgs)
        def report = deleteOpenSearchConfigurationFunction.execute()

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Error while committing delete configuration with given pid'() {
        when:
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchConfigurationFunction.setArguments(functionArgs)
        def report = deleteOpenSearchConfigurationFunction.execute()

        then:
        !report.getResult().getValue()
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).code == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = deleteOpenSearchConfigurationFunction.execute()

        then:
        report.getResult() == null
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.getErrorMessages()*.getPath() == [PID_PATH]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        DeleteOpenSearchConfiguration deleteOpenSearchNoExistingConfig = new DeleteOpenSearchConfiguration(configuratorSuite)
        serviceActions.read(S_PID) >> [:]
        deleteOpenSearchNoExistingConfig.setArguments(functionArgs)

        DeleteOpenSearchConfiguration deleteOpenSearchFailPersist = new DeleteOpenSearchConfiguration(configuratorSuite)
        serviceActions.read(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchFailPersist.setArguments(functionArgs)

        when:
        def errorCodes = deleteOpenSearchConfigurationFunction.getFunctionErrorCodes()
        def noExistingConfigReport = deleteOpenSearchNoExistingConfig.execute()
        def failedPersistReport = deleteOpenSearchFailPersist.execute()

        then:
        errorCodes.size() == 2
        errorCodes.contains(noExistingConfigReport.getErrorMessages().get(0).getCode())
        errorCodes.contains(failedPersistReport.getErrorMessages().get(0).getCode())
    }
}
