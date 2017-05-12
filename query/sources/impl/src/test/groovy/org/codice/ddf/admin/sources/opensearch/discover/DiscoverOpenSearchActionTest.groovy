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
package org.codice.ddf.admin.sources.opensearch.discover

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.common.ReportWithResult
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.common.message.ErrorMessage
import org.codice.ddf.admin.sources.commons.utils.OpenSearchSourceUtils
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.discoverByAddressActionArgs
import static org.codice.ddf.admin.sources.SourceTestCommons.discoverByUrlActionArgs
import static org.codice.ddf.admin.sources.SourceTestCommons.ADDRESS
import static org.codice.ddf.admin.sources.SourceTestCommons.CREDENTIALS
import static org.codice.ddf.admin.sources.SourceTestCommons.HOST
import static org.codice.ddf.admin.sources.SourceTestCommons.HOSTNAME
import static org.codice.ddf.admin.sources.SourceTestCommons.PASSWORD
import static org.codice.ddf.admin.sources.SourceTestCommons.PORT
import static org.codice.ddf.admin.sources.SourceTestCommons.URL_NAME
import static org.codice.ddf.admin.sources.SourceTestCommons.USERNAME
import static org.codice.ddf.admin.sources.SourceTestCommons.refreshDiscoverByAddressActionArgs
import static org.codice.ddf.admin.sources.SourceTestCommons.refreshDiscoverByUrlActionArgs

class DiscoverOpenSearchActionTest extends Specification {

    Action discoverOpenSearch

    OpenSearchSourceUtils openSearchSourceUtils

    static BASE_PATH = [DiscoverOpenSearchAction.ID, BaseAction.ARGUMENT]

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
        openSearchSourceUtils = Mock(OpenSearchSourceUtils)
        discoverOpenSearch = new DiscoverOpenSearchAction(openSearchSourceUtils)
    }

    def 'test successful discover using URL'() {
        setup:
        discoverOpenSearch.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        1 * openSearchSourceUtils.getOpenSearchConfig(_ , _) >> createResult(false, SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'test successful discover using hostname and port'() {
        setup:
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        1 * openSearchSourceUtils.discoverOpenSearchUrl(_, _) >> createResult(false, UrlField.class)
        1 * openSearchSourceUtils.getOpenSearchConfig(_, _) >> createResult(false, SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'test failure discovery using URL while getting preferred config'() {
        setup:
        discoverOpenSearch.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        1 * openSearchSourceUtils.getOpenSearchConfig(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'test failure when using hostname and port when discovering the URL'() {
        setup:
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        1 * openSearchSourceUtils.discoverOpenSearchUrl(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'test failure when using hostname and port when getting preferred config'() {
        setup:
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        1 * openSearchSourceUtils.discoverOpenSearchUrl(_, _) >> createResult(false, UrlField.class)
        1 * openSearchSourceUtils.getOpenSearchConfig(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'test missing required URL field when no arguments provided'() {
        when:
        def report = discoverOpenSearch.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == URL_FIELD_PATH
    }

    def 'test missing required port field when hostname provided and url is not'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(PORT, null)
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == PORT_FIELD_PATH
    }

    def 'test missing required hostname field when port is provided and url is not'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(HOSTNAME, null)
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == HOSTNAME_FIELD_PATH
    }

    def 'test failure due to invalid url'() {
        setup:
        discoverByUrlActionArgs.get(ADDRESS).put(URL_NAME, 'n19($')
        discoverOpenSearch.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_URL_ERROR
        report.messages().get(0).path == URL_FIELD_PATH
    }

    def 'test failure due to invalid hostname'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(HOSTNAME, 'f089d**40')
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_HOSTNAME
        report.messages().get(0).path == HOSTNAME_FIELD_PATH
    }

    def 'test failure due to invalid port'() {
        setup:
        discoverByAddressActionArgs.get(ADDRESS).get(HOST).put(PORT, -1)
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_PORT_RANGE
        report.messages().get(0).path == PORT_FIELD_PATH
    }

    def 'test failure due to credentials provided but empty'() {
        setup:
        discoverByAddressActionArgs.put(CREDENTIALS, [(PASSWORD):'',(USERNAME): ''])
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

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
