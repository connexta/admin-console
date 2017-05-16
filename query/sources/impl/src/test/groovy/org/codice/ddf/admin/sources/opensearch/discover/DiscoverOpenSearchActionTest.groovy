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

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverOpenSearchActionTest extends Specification {

    Action discoverOpenSearch

    OpenSearchSourceUtils openSearchSourceUtils

    static BASE_PATH = [DiscoverOpenSearchAction.ID, BaseAction.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOST_FIELD_PATH = [ADDRESS_FIELD_PATH, HOST].flatten()

    static PORT_FIELD_PATH = [HOST_FIELD_PATH, PORT].flatten()

    static HOSTNAME_FIELD_PATH = [HOST_FIELD_PATH, HOSTNAME].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def setup() {
        refreshDiscoverByAddressActionArgs()
        refreshDiscoverByUrlActionArgs()
        openSearchSourceUtils = Mock(OpenSearchSourceUtils)
        discoverOpenSearch = new DiscoverOpenSearchAction(openSearchSourceUtils)
    }

    def 'successful discover using URL'() {
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

    def 'successful discover using hostname and port'() {
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

    def 'failure discovery using URL while getting preferred config'() {
        setup:
        discoverOpenSearch.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        1 * openSearchSourceUtils.getOpenSearchConfig(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'failure when using hostname and port when discovering the URL'() {
        setup:
        discoverOpenSearch.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverOpenSearch.process()

        then:
        1 * openSearchSourceUtils.discoverOpenSearchUrl(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'failure when using hostname and port when getting preferred config'() {
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

    def 'fail when missing required fields'() {
        when:
        def report = discoverOpenSearch.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 1
        report.messages()*.getPath() == [URL_FIELD_PATH]
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
