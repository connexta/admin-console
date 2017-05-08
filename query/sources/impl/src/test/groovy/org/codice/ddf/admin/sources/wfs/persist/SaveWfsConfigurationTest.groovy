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
package org.codice.ddf.admin.sources.wfs.persist

import ddf.catalog.source.FederatedSource
import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.commons.SourceMessages
import org.codice.ddf.admin.sources.fields.WfsVersion
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class SaveWfsConfigurationTest extends Specification {

    static BASE_PATH = [SaveWfsConfiguration.ID, BaseAction.ARGUMENT]

    static CONFIG_PATH = [BASE_PATH, SOURCE_CONFIG].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SOURCE_NAME].flatten()

    static PID_PATH = [BASE_PATH, PID].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static WFS_VERSION = WfsVersion.DEFAULT_FIELD_NAME;

    static WFS_VERSION_PATH = [CONFIG_PATH, WFS_VERSION].flatten()

    static TEST_WFS_VERSION = '1.0.0'

    Action saveWfsConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ConfigReader configReader

    FederatedSource federatedSource

    def actionArgs

    def federatedSources = []

    def setup() {
        actionArgs = createWfsSaveArgs()
        configReader = Mock(ConfigReader)
        configurator = Mock(Configurator)
        federatedSource = Mock(FederatedSource)
        federatedSource.getId() >> TEST_SOURCENAME
        federatedSources.add(federatedSource)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getConfigReader() >> configReader
            getConfigurator() >> configurator
        }
        saveWfsConfiguration = new SaveWfsConfiguration(configuratorFactory)
    }

    def 'test new configuration save successful'() {
        setup:
        saveWfsConfiguration.setArguments(actionArgs)
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == true
    }

    def 'test fail to save new config due to duplicate source name'() {
        setup:
        saveWfsConfiguration.setArguments(actionArgs)
        configReader.getServices(_, _) >> federatedSources

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
        report.messages().get(0).path == SOURCE_NAME_PATH
    }

    def 'test fail to save new config due to failure to commit'() {
        setup:
        saveWfsConfiguration.setArguments(actionArgs)
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
        report.messages().get(0).path == CONFIG_PATH
    }

    def 'test update configuration successful'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        configReader.getConfig(_ as String) >> [(ID):TEST_SOURCENAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == true
    }

    def 'test fail update due to existing source name'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [(ID):'someOtherSourceName']
        configReader.getServices(_, _) >> federatedSources

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
        report.messages().get(0).path == SOURCE_NAME_PATH
    }

    def 'test fail update due to failure to commit'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        configReader.getConfig(_) >> [(ID):TEST_SOURCENAME]
        configReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).path == PID_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_UPDATE_ERROR
    }

    def 'test fail update config due to no existing config specified by pid'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        configReader.getConfig(S_PID) >> [:]

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == PID_PATH
    }

    def 'test fail update due to provided but empty pid'() {
        setup:
        actionArgs.put(PID, '')
        saveWfsConfiguration.setArguments(actionArgs)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == PID_PATH
    }

    def 'test fail save due to missing required source name field'() {
        setup:
        actionArgs.get(SOURCE_CONFIG).put(SOURCE_NAME, null)
        saveWfsConfiguration.setArguments(actionArgs)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == SOURCE_NAME_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def 'test fail save due to missing required endpoint url field'() {
        setup:
        actionArgs.get(SOURCE_CONFIG).put(ENDPOINT_URL, null)
        saveWfsConfiguration.setArguments(actionArgs)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == ENDPOINT_URL_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def 'test fail save due to missing required wfsVersion field'() {
        setup:
        actionArgs.get(SOURCE_CONFIG).put(WFS_VERSION, null)
        saveWfsConfiguration.setArguments(actionArgs)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == WFS_VERSION_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def 'test fail due to unsupported wfs version'() {
        setup:
        actionArgs.get(SOURCE_CONFIG).put(WFS_VERSION, '1.2.3')
        saveWfsConfiguration.setArguments(actionArgs)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == WFS_VERSION_PATH
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
    }

    def createWfsSaveArgs() {
        refreshSaveConfigActionArgs()
        actionArgs = saveConfigActionArgs
        actionArgs.get(SOURCE_CONFIG).put(WFS_VERSION, TEST_WFS_VERSION)
        return actionArgs
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
