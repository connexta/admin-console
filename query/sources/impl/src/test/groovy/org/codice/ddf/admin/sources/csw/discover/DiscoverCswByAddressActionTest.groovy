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
import org.codice.ddf.admin.common.fields.common.AddressField
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.common.message.ErrorMessage
import org.codice.ddf.admin.sources.commons.utils.CswSourceUtils
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.discoverByAddressActionArgs
import static org.codice.ddf.admin.sources.SourceTestCommons.refreshDiscoverByAddressActionArgs

class DiscoverCswByAddressActionTest extends Specification {

    Action discoverCswByAddressAction

    CswSourceUtils cswSourceUtils

    static BASE_PATH = [DiscoverCswByAddressAction.ID, BaseAction.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, AddressField.DEFAULT_FIELD_NAME].flatten()

    static CREDENTIALS_FIELD_PATH = [BASE_PATH, CredentialsField.DEFAULT_FIELD_NAME].flatten()

    static PORT_FIELD_PATH = [ADDRESS_FIELD_PATH, PortField.DEFAULT_FIELD_NAME].flatten()

    static HOSTNAME_FIELD_PATH = [ADDRESS_FIELD_PATH, HostnameField.DEFAULT_FIELD_NAME].flatten()

    static USERNAME_FIELD_PATH = [CREDENTIALS_FIELD_PATH, CredentialsField.USERNAME].flatten()

    static PASSWORD_FIELD_PATH = [CREDENTIALS_FIELD_PATH, CredentialsField.PASSWORD].flatten()

    def setup() {
        refreshDiscoverByAddressActionArgs()
        cswSourceUtils = Mock(CswSourceUtils)
        discoverCswByAddressAction = new DiscoverCswByAddressAction(cswSourceUtils)
        discoverCswByAddressAction.setArguments(discoverByAddressActionArgs)
    }

    def 'test configurator errors during discover csw url'() {
        when:
        def result = discoverCswByAddressAction.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_ as Field, _ as Field) >> createResult(true, [AddressField.DEFAULT_FIELD_NAME], null)
        result.result() == null
        result.messages().get(0).path == ADDRESS_FIELD_PATH
    }

    def 'test errors during getting preferred csw config'() {
        when:
        def result = discoverCswByAddressAction.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_ as Field, _ as Field) >> createResult(false, [], UrlField.class)
        1 * cswSourceUtils.getPreferredCswConfig(_ as Field, _ as Field) >> createResult(true, [AddressField.DEFAULT_FIELD_NAME], null)
        result.result() == null
        result.messages().get(0).path == ADDRESS_FIELD_PATH
    }

    def 'test csw successfully discovered'() {
        when:
        def report = discoverCswByAddressAction.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_ as Field, _ as Field) >> createResult(false, [], UrlField.class)
        1 * cswSourceUtils.getPreferredCswConfig(_ as Field, _ as Field) >> createResult(false, [], SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).sourceHandlerName() == DiscoverCswByAddressAction.ID
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'test failure due to missing required address field'() {
        setup:
        discoverByAddressActionArgs.put(AddressField.DEFAULT_FIELD_NAME, [(PortField.DEFAULT_FIELD_NAME):null,(HostnameField.DEFAULT_FIELD_NAME): null])
        discoverCswByAddressAction.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCswByAddressAction.process()

        then:
        report.result() == null
        report.messages().size() == 2
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == HOSTNAME_FIELD_PATH
        report.messages().get(1).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(1).path == PORT_FIELD_PATH
    }

    def 'test failure due to provided credentials that are empty'() {
        setup:
        discoverByAddressActionArgs.put(CredentialsField.DEFAULT_FIELD_NAME, [(CredentialsField.PASSWORD):'',(CredentialsField.USERNAME): ''])
        discoverCswByAddressAction.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverCswByAddressAction.process()

        then:
        report.result() == null
        report.messages().size() == 2
        report.messages().get(0).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(0).path == USERNAME_FIELD_PATH
        report.messages().get(1).code == DefaultMessages.EMPTY_FIELD
        report.messages().get(1).path == PASSWORD_FIELD_PATH
    }

    def createResult(boolean hasError, List path, Class clazz) {
        if(hasError) {
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
