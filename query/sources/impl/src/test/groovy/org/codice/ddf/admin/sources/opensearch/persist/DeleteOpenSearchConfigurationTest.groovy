package org.codice.ddf.admin.sources.opensearch.persist

import org.codice.ddf.admin.api.FieldProvider
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DeleteOpenSearchConfigurationTest extends Specification {

    FieldProvider deleteOpenSearchConfigurationAction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    Configurator configurator

    static RESULT_ARGUMENT_PATH = [DeleteOpenSearchConfiguration.ID]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, FunctionField.ARGUMENT].flatten()

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

    def 'Successfully deleting WFS config returns true'() {
        when:
        configReader.getConfig(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(false)
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() != null
        report.result().getValue() == true
    }

    def 'Fail delete when no existing configuration with the provided pid'() {
        when:
        configReader.getConfig(S_PID) >> [:]
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Error while committing delete configuration with given pid'() {
        when:
        configReader.getConfig(S_PID) >> configToBeDeleted
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_DELETE_ERROR
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail when missing required fields'() {
        when:
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [PID_PATH]
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
