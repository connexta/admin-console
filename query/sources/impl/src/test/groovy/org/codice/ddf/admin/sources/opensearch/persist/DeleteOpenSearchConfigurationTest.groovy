package org.codice.ddf.admin.sources.opensearch.persist

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.services.CswServiceProperties
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.SERVICE_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.F_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.SOURCE_ID_1
import static org.codice.ddf.admin.sources.SourceTestCommons.S_PID
import static org.codice.ddf.admin.sources.SourceTestCommons.TEST_USERNAME
import static org.codice.ddf.admin.sources.SourceTestCommons.deleteConfigActionArgs

class DeleteOpenSearchConfigurationTest extends Specification {

    static BASE_PATH = [DeleteOpenSearchConfiguration.ID, BaseAction.ARGUMENT]

    static SERVICE_PID_PATH = [BASE_PATH, SERVICE_PID].flatten()

    Action deleteOpenSearchConfigurationAction

    ConfiguratorFactory configuratorFactory

    ConfigReader configReader

    Configurator configurator

    def actionArgs = [
        (SERVICE_PID) : S_PID
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

    def 'test success delete config returns deleted config source info'() {
        when:
        configReader.getConfig(S_PID) >> deleteConfigActionArgs
        configurator.commit(_, _) >> mockReport(false)
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() != null
//        assertConfig(report.result(), DeleteOpenSearchConfiguration.ID, deleteConfigActionArgs, S_PID)
    }

    def 'test no config found with provided service pid'() {
        when:
        configReader.getConfig(S_PID) >> [:]
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def 'test error while committing delete configuration with given service pid'() {
        when:
        configReader.getConfig(S_PID) >> deleteConfigActionArgs
        configurator.commit(_, _) >> mockReport(true)
        deleteOpenSearchConfigurationAction.setArguments(actionArgs)
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_DELETE_ERROR
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def 'test failure due to required service pid argument not provided'() {
        when:
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def 'test failure due to service pid argument provided but empty'() {
        when:
        deleteOpenSearchConfigurationAction.setArguments([(SERVICE_PID):''])
        def report = deleteOpenSearchConfigurationAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == SERVICE_PID_PATH
    }

    def assertConfig(Field field, String actionId, Map<String, Object> properties, String servicePid) {
        def sourceInfo = (SourceInfoField) field
        assert !sourceInfo.isAvailable()
        assert sourceInfo.sourceHandlerName() == actionId
        assert sourceInfo.config().endpointUrl() == properties.get(CswServiceProperties.CSW_URL)
        assert sourceInfo.config().credentials().password() == "*****"
        assert sourceInfo.config().credentials().username() == TEST_USERNAME
        assert sourceInfo.config().sourceName() == SOURCE_ID_1
        assert sourceInfo.config().factoryPid() == F_PID
        assert sourceInfo.config().servicePid() == servicePid
        return true
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
