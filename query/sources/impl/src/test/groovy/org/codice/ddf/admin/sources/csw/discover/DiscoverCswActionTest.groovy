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
 **/
package org.codice.ddf.admin.sources.csw.discover

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.common.ReportWithResult
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.common.message.ErrorMessage
import org.codice.ddf.admin.sources.commons.utils.CswSourceUtils
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.ADDRESS
import static org.codice.ddf.admin.sources.SourceTestCommons.HOST
import static org.codice.ddf.admin.sources.SourceTestCommons.CREDENTIALS
import static org.codice.ddf.admin.sources.SourceTestCommons.HOSTNAME
import static org.codice.ddf.admin.sources.SourceTestCommons.PASSWORD
import static org.codice.ddf.admin.sources.SourceTestCommons.PORT
import static org.codice.ddf.admin.sources.SourceTestCommons.URL_NAME
import static org.codice.ddf.admin.sources.SourceTestCommons.USERNAME
import static org.codice.ddf.admin.sources.SourceTestCommons.discoverByAddressActionArgs
import static org.codice.ddf.admin.sources.SourceTestCommons.discoverByUrlActionArgs
import static org.codice.ddf.admin.sources.SourceTestCommons.refreshDiscoverByAddressActionArgs
import static org.codice.ddf.admin.sources.SourceTestCommons.refreshDiscoverByUrlActionArgs

class DiscoverCswActionTest extends Specification {

    Action discoverCsw

    CswSourceUtils cswSourceUtils

    static BASE_PATH = [DiscoverCswAction.ID, BaseAction.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOST_FIELD_PATH = [ADDRESS_FIELD_PATH, HOST].flatten()

    static PORT_FIELD_PATH = [HOST_FIELD_PATH, PORT].flatten()

    static HOSTNAME_FIELD_PATH = [HOST_FIELD_PATH, HOSTNAME].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    static CREDENTIALS_FIELD_PATH = [BASE_PATH, CREDENTIALS].flatten()

    static USERNAME_FIELD_PATH = [CREDENTIALS_FIELD_PATH, USERNAME].flatten()

    static PASSWORD_FIELD_PATH = [CREDENTIALS_FIELD_PATH, PASSWORD].flatten()

    def setup() {
        refreshDiscoverByAddressActionArgs()
        refreshDiscoverByUrlActionArgs()
        cswSourceUtils = Mock(CswSourceUtils)
        discoverCsw = new DiscoverCswAction(cswSourceUtils)
    }

    def 'test successful discover using URL'() {
        setup:
        discoverCsw.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.getPreferredCswConfig(_ , _) >> createResult(false, SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'test successful discover using hostname and port'() {
        setup:
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_, _) >> createResult(false, UrlField.class)
        1 * cswSourceUtils.getPreferredCswConfig(_, _) >> createResult(false, SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'test failure discovery using URL while getting preferred config'() {
        setup:
        discoverCsw.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.getPreferredCswConfig(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'test failure when using hostname+port when getting discovering the URL'() {
        setup:
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'test failure discovery using hostname+port while getting preferred config'() {
        setup:
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_, _) >> createResult(false, UrlField.class)
        1 * cswSourceUtils.getPreferredCswConfig(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'test missing required URL field when no arguments provided'() {
        when:
        def report = discoverCsw.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == URL_FIELD_PATH
    }

    def 'test missing required port field when hostname provided and url is not'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(PORT, null)
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == PORT_FIELD_PATH
    }

    def 'test missing required hostname field when port provided and url is not'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(HOSTNAME, null)
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == HOSTNAME_FIELD_PATH
    }

    def 'test failure due to invalid url'() {
        setup:
        discoverByUrlActionArgs.get(ADDRESS).put(URL_NAME, 'n19($')
        discoverCsw.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_URL_ERROR
        report.messages().get(0).path == URL_FIELD_PATH
    }

    def 'test failure due to invalid hostname'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(HOSTNAME, 'f089d**40')
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_HOSTNAME
        report.messages().get(0).path == HOSTNAME_FIELD_PATH
    }

    def 'test failure due to invalid port'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(PORT, -1)
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_PORT_RANGE
        report.messages().get(0).path == PORT_FIELD_PATH
    }

    def 'test failure due to credentials provided but empty'() {
        setup:
        discoverByAddressActionArgs.put(CREDENTIALS, [(PASSWORD):'',(USERNAME): ''])
        discoverCsw.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCsw.process()

        then:
        report.result() == null
        report.messages().size() == 2
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == USERNAME_FIELD_PATH
        report.messages().get(1).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(1).path == PASSWORD_FIELD_PATH
    }

    def createResult(boolean hasError, Class clazz) {
        if(hasError) {
            return Mock(ReportWithResult) {
                argumentMessages() >> [new ErrorMessage("code", [])]
                resultMessages() >> []
            }
        }
        return Mock(ReportWithResult) {
            argumentMessages() >> []
            resultMessages() >> []
            result() >> Mock(clazz) {
                path() >> []
                credentials() >> Mock(CredentialsField)
            }
        }
    }
}
