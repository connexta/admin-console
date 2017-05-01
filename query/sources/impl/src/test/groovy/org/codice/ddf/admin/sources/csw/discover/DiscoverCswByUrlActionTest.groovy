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
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.common.Result
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.common.message.ErrorMessage
import org.codice.ddf.admin.sources.commons.utils.CswSourceUtils
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

class DiscoverCswByUrlActionTest extends Specification {

    Action discoverCswUrlByAction

    CswSourceUtils cswSourceUtils

    static ENDPOINT_URL_PATH = [DiscoverCswByUrlAction.ID, BaseAction.ARGUMENT, SourceConfigUnionField.ENDPOINT_URL_FIELD]

    def actionArgs

    def setup() {
        refreshActionArgs();
        cswSourceUtils = Mock(CswSourceUtils)
        discoverCswUrlByAction = new DiscoverCswByUrlAction(cswSourceUtils)
        discoverCswUrlByAction.setArguments(actionArgs)
    }

    def 'test configurator errors while getting preferred config'() {
        when:
        def result = discoverCswUrlByAction.process()

        then:
        1 * cswSourceUtils.getPreferredCswConfig(_ as Field, _ as Field) >> createResult(true, [UrlField.DEFAULT_FIELD_NAME], null)
        result.result() == null
        result.messages().get(0).path == [DiscoverCswByUrlAction.ID, BaseAction.ARGUMENT, UrlField.DEFAULT_FIELD_NAME]
    }

    def 'test successful preferred csw config found'() {
        when:
        def result = discoverCswUrlByAction.process()

        then:
        1 * cswSourceUtils.getPreferredCswConfig(_ as Field, _ as Field) >> createResult(false, [UrlField.DEFAULT_FIELD_NAME], SourceConfigUnionField.class)
        result.result() instanceof SourceInfoField
        ((SourceInfoField) result.result()).sourceHandlerName() == DiscoverCswByUrlAction.ID
        ((SourceInfoField) result.result()).isAvailable()
        ((SourceInfoField) result.result()).config() != null
    }

    def 'test failure due to missing required endpoint url field'() {
        setup:
        actionArgs.put(SourceConfigUnionField.ENDPOINT_URL_FIELD, null)
        discoverCswUrlByAction.setArguments(actionArgs)

        when:
        def result = discoverCswUrlByAction.process()

        then:
        result.result() == null
        result.messages().size() == 1
        result.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        result.messages().get(0).path == ENDPOINT_URL_PATH
    }

    def 'test failure due to empty endpoint url field'() {
        setup:
        actionArgs.put(SourceConfigUnionField.ENDPOINT_URL_FIELD, '')
        discoverCswUrlByAction.setArguments(actionArgs)

        when:
        def result = discoverCswUrlByAction.process()

        then:
        result.result() == null
        result.messages().size() == 1
        result.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        result.messages().get(0).path == ENDPOINT_URL_PATH
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

    def refreshActionArgs() {
        actionArgs = [
            (SourceConfigUnionField.ENDPOINT_URL_FIELD): "http://localhost:8993/sevices/csw",
            (CredentialsField.DEFAULT_FIELD_NAME)      : [
                username: "admin",
                password: "admin"
            ]
        ]
    }
}
