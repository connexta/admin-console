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
import org.codice.ddf.admin.configurator.Configurator
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.commons.SourceMessages
import org.codice.ddf.admin.sources.fields.WfsVersion
import org.codice.ddf.internal.admin.configurator.opfactory.AdminOpFactory
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory
import org.codice.ddf.internal.admin.configurator.opfactory.ServiceReader
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class SaveWfsConfigurationTest extends Specification {

    static RESULT_ARGUMENT_PATH = [SaveWfsConfiguration.ID]

    static BASE_PATH = [RESULT_ARGUMENT_PATH, BaseAction.ARGUMENT].flatten()

    static CONFIG_PATH = [BASE_PATH, SOURCE_CONFIG].flatten()

    static SOURCE_NAME_PATH = [CONFIG_PATH, SOURCE_NAME].flatten()

    static ENDPOINT_URL_PATH = [CONFIG_PATH, ENDPOINT_URL].flatten()

    static WFS_VERSION = WfsVersion.DEFAULT_FIELD_NAME;

    static WFS_VERSION_PATH = [CONFIG_PATH, WFS_VERSION].flatten()

    static TEST_WFS_VERSION = WfsVersion.WFS_VERSION_1

    Action saveWfsConfiguration

    ConfiguratorFactory configuratorFactory

    Configurator configurator

    ServiceReader serviceReader

    AdminOpFactory adminOpFactory

    ManagedServiceOpFactory managedServiceOpFactory

    FederatedSource federatedSource

    def actionArgs

    def federatedSources = []

    def setup() {
        actionArgs = createWfsSaveArgs()
        serviceReader = Mock(ServiceReader)
        adminOpFactory = Mock(AdminOpFactory)
        managedServiceOpFactory = Mock(ManagedServiceOpFactory)
        configurator = Mock(Configurator)
        federatedSource = Mock(FederatedSource)
        federatedSource.getId() >> TEST_SOURCENAME
        federatedSources.add(federatedSource)
        configuratorFactory = Mock(ConfiguratorFactory) {
            getServiceReader() >> serviceReader
            getConfigurator() >> configurator
        }
        saveWfsConfiguration = new SaveWfsConfiguration(configuratorFactory, adminOpFactory, managedServiceOpFactory, serviceReader)
    }

    def 'Successfully save new WFS configuration'() {
        setup:
        saveWfsConfiguration.setArguments(actionArgs)
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == true
    }

    def 'Fail to save new WFS config due to duplicate source name'() {
        setup:
        saveWfsConfiguration.setArguments(actionArgs)
        serviceReader.getServices(_, _) >> federatedSources

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
        report.messages().get(0).path == SOURCE_NAME_PATH
    }

    def 'Fail to save new WFS config due to failure to commit'() {
        setup:
        saveWfsConfiguration.setArguments(actionArgs)
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.FAILED_PERSIST
        report.messages().get(0).path == CONFIG_PATH
    }

    def 'Successfully update WFS configuration'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        adminOpFactory.read(_ as String) >> [(ID):TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(false)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == true
    }

    def 'Fail to update due to existing source name specified by pid'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        adminOpFactory.read(_) >> [(ID):'updatedName']
        serviceReader.getServices(_, _) >> [new TestSource(S_PID, 'updatedName', false), new TestSource("existingSource", TEST_SOURCENAME, false)]

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == SourceMessages.DUPLICATE_SOURCE_NAME
        report.messages().get(0).path == SOURCE_NAME_PATH
    }

    def 'Fail configuration update due to failure to commit'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        adminOpFactory.read(_) >> [(ID):TEST_SOURCENAME]
        serviceReader.getServices(_, _) >> []
        configurator.commit(_, _) >> mockReport(true)

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result().getValue() == false
        report.messages().size() == 1
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
        report.messages().get(0).code == DefaultMessages.FAILED_UPDATE_ERROR
    }

    def 'Fail to update WFS config due to no existing config specified by pid'() {
        setup:
        actionArgs.put(PID, S_PID)
        saveWfsConfiguration.setArguments(actionArgs)
        adminOpFactory.read(S_PID) >> [:]

        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.NO_EXISTING_CONFIG
        report.messages().get(0).path == RESULT_ARGUMENT_PATH
    }

    def 'Fail due to missing required fields'() {
        when:
        def report = saveWfsConfiguration.process()

        then:
        report.result() == null
        report.messages().size() == 3
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 3
        report.messages()*.getPath() == [SOURCE_NAME_PATH, ENDPOINT_URL_PATH, WFS_VERSION_PATH]
    }

    def createWfsSaveArgs() {
        actionArgs = getBaseSaveConfigActionArgs()
        actionArgs.get(SOURCE_CONFIG).put(WFS_VERSION, TEST_WFS_VERSION)
        return actionArgs
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }
}
