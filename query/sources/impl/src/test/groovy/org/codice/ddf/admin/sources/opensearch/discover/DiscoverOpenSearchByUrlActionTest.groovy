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
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.common.Result
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.common.message.ErrorMessage
import org.codice.ddf.admin.sources.commons.utils.OpenSearchSourceUtils
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverOpenSearchByUrlActionTest extends Specification {

    Action discoverOpenSearchByUrlAction

    OpenSearchSourceUtils openSearchSourceUtils

    static BASE_PATH = [DiscoverOpenSearchByUrlAction.ID, BaseAction.ARGUMENT]

    static URL_PATH = [BASE_PATH, URL].flatten()

    static ENDPOINT_URL_PATH = [BASE_PATH, ENDPOINT_URL].flatten()

    static CREDENTIALS_FIELD_PATH = [BASE_PATH, CREDENTIALS].flatten()

    static USERNAME_FIELD_PATH = [CREDENTIALS_FIELD_PATH, USERNAME].flatten()

    static PASSWORD_FIELD_PATH = [CREDENTIALS_FIELD_PATH, PASSWORD].flatten()

    def setup() {
        refreshDiscoverByUrlActionArgs()
        openSearchSourceUtils = Mock(OpenSearchSourceUtils)
        discoverOpenSearchByUrlAction = new DiscoverOpenSearchByUrlAction(openSearchSourceUtils)
        discoverOpenSearchByUrlAction.setArguments(discoverByUrlActionArgs)
    }

    def 'test errors while getting preferred config'() {
        when:
        def report = discoverOpenSearchByUrlAction.process()

        then:
        1 * openSearchSourceUtils.getOpenSearchConfig(_ as Field, _ as Field) >> createResult(true, [URL], null)
        report.result() == null
        report.messages().get(0).path == URL_PATH
    }

    def 'test successfuly preferred open search config found'() {
        when:
        def report = discoverOpenSearchByUrlAction.process()

        then:
        1 * openSearchSourceUtils.getOpenSearchConfig(_ as Field, _ as Field) >> createResult(false, [URL], SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).sourceHandlerName() == DiscoverOpenSearchByUrlAction.ID
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'test failure due to missing required endpoint url field'() {
        setup:
        discoverByUrlActionArgs.put(ENDPOINT_URL, null)
        discoverOpenSearchByUrlAction.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverOpenSearchByUrlAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == ENDPOINT_URL_PATH
    }

    def 'test failure due to invalid endpoint url'() {
        setup:
        discoverByUrlActionArgs.put(ENDPOINT_URL, 'notAUrl')
        discoverOpenSearchByUrlAction.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverOpenSearchByUrlAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_URL_ERROR
        report.messages().get(0).path == ENDPOINT_URL_PATH
    }

    def 'test failure due to credentials provided but empty'() {
        setup:
        discoverByUrlActionArgs.put(CREDENTIALS, [(PASSWORD): '', (USERNAME): ''])
        discoverOpenSearchByUrlAction.setArguments(discoverByUrlActionArgs)

        when:
        def report = discoverOpenSearchByUrlAction.process()

        then:
        report.result() == null
        report.messages().size() == 2
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == USERNAME_FIELD_PATH
        report.messages().get(1).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(1).path == PASSWORD_FIELD_PATH
    }

    def createResult(boolean hasError, List path, Class clazz) {
        if (hasError) {
            return new Result().argumentMessage(new ErrorMessage("code", path))
        }
        return Mock(Result) {
            argumentMessages() >> []
            get() >> Mock(clazz) {
                path() >> path
                credentials() >> Mock(CredentialsField)
            }
        }
    }
}
