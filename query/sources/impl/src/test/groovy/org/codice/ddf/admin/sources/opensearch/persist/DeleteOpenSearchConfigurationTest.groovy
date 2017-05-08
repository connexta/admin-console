package org.codice.ddf.admin.sources.opensearch.persist

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DeleteOpenSearchConfigurationTest extends Specification {

    Action deleteOpenSearchConfigurationAction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    Configurator configurator

    static BASE_PATH = [DeleteOpenSearchConfiguration.ID, BaseAction.ARGUMENT]

    static PID_PATH = [BASE_PATH, PID].flatten()

    def actionArgs = [
        (PID): S_PID
    ]

    def setup() {
        configReader = Mock(ConfigReader)
        configurator = Mock(Configurator)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigurator() >> configurator
            getConfigReader() >> configReader
        }
        deleteOpenSearchConfigurationAction = new DeleteOpenSearchConfiguration(configuratorFactory)
    }

    def 'test success delete config returns true'() {
        when:
        configReader.getConfig(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(false)
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() != null
        report.result().getValue() == true
    }

    def 'test no config found with provided pid'() {
        when:
        configReader.getConfig(S_PID) >> [:]
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == PID_PATH
    }

    def 'test error while committing delete configuration with given pid'() {
        when:
        configReader.getConfig(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_DELETE_ERROR
        report.messages().get(0).path == PID_PATH
    }

    def 'test failure due to required pid argument not provided'() {
        when:
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == PID_PATH
    }

    def 'test failure due to pid argument provided but empty'() {
        when:
        deleteOpenSearchConfigurationAction.setArguments([(PID):''])
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == PID_PATH
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
