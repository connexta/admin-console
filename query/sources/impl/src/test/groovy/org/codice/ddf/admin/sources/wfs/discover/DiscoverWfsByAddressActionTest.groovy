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
package org.codice.ddf.admin.sources.wfs.discover

import org.codice.ddf.admin.api.action.Action
import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.common.ReportWithResult
import org.codice.ddf.admin.common.actions.BaseAction
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.message.DefaultMessages
import org.codice.ddf.admin.common.message.ErrorMessage
import org.codice.ddf.admin.sources.commons.utils.WfsSourceUtils
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverWfsByAddressActionTest extends Specification {

    Action discoverWfsAbyAddressAction

    WfsSourceUtils wfsSourceUtils

    static BASE_PATH = [DiscoverWfsByAddressAction.ID, BaseAction.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static HOSTNAME_FIELD_PATH = [ADDRESS_FIELD_PATH, HOSTNAME].flatten()

    static PORT_FIELD_PATH = [ADDRESS_FIELD_PATH, PORT].flatten()

    static CREDENTIALS_FIELD_PATH = [BASE_PATH, CREDENTIALS].flatten()

    static PASSWORD_FIELD_PATH = [CREDENTIALS_FIELD_PATH, PASSWORD].flatten()

    static USERNAME_FIELD_PATH = [CREDENTIALS_FIELD_PATH, USERNAME].flatten()

    def setup() {
        refreshDiscoverByAddressActionArgs()
        wfsSourceUtils = Mock(WfsSourceUtils)
        discoverWfsAbyAddressAction = new DiscoverWfsByAddressAction(wfsSourceUtils)
        discoverWfsAbyAddressAction.setArguments(discoverByAddressActionArgs)
    }

    def 'test errors during discover wfs url'() {
        when:
        def report = discoverWfsAbyAddressAction.process()

        then:
        1 * wfsSourceUtils.discoverWfsUrl(_ as Field, _ as Field) >> createResult(true, [ADDRESS], null)
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == ADDRESS_FIELD_PATH
    }

    def 'test errors during getting preferred wfs config'() {
        when:
        def report = discoverWfsAbyAddressAction.process()

        then:
        1 * wfsSourceUtils.discoverWfsUrl(_ as Field, _ as Field) >> createResult(false, [], UrlField.class)
        1 * wfsSourceUtils.getPreferredWfsConfig(_ as Field, _ as Field) >> createResult(true, [ADDRESS], null)
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).path == ADDRESS_FIELD_PATH
    }

    def 'test csw successfully discovered'() {
        when:
        def report = discoverWfsAbyAddressAction.process()

        then:
        1 * wfsSourceUtils.discoverWfsUrl(_ as Field, _ as Field) >> createResult(false, [], UrlField.class)
        1 * wfsSourceUtils.getPreferredWfsConfig(_ as Field, _ as Field) >> createResult(false, [], SourceConfigUnionField.class)
        report.result() != null
        ((SourceInfoField) report.result()).sourceHandlerName() == DiscoverWfsByAddressAction.ID
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'test failure due to missing required address field'() {
        setup:
        discoverByAddressActionArgs.put(ADDRESS, [(PORT):null,(HOSTNAME): null])
        discoverWfsAbyAddressAction.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverWfsAbyAddressAction.process()

        then:
        report.result() == null
        report.messages().size() == 2
        report.messages().get(0).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(0).path == HOSTNAME_FIELD_PATH
        report.messages().get(1).code == DefaultMessages.MISSING_REQUIRED_FIELD
        report.messages().get(1).path == PORT_FIELD_PATH
    }

    def 'test failure due to invalid hostname' () {
        setup:
        discoverByAddressActionArgs.put(ADDRESS, [(PORT):8993,(HOSTNAME): 'h0s7n4m3!!'])
        discoverWfsAbyAddressAction.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverWfsAbyAddressAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_HOSTNAME
        report.messages().get(0).path == HOSTNAME_FIELD_PATH
    }

    def 'test failure due to invalid port range'() {
        setup:
        discoverByAddressActionArgs.put(ADDRESS, [(PORT):port,(HOSTNAME): 'localhost'])
        discoverWfsAbyAddressAction.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverWfsAbyAddressAction.process()

        then:
        report.result() == null
        report.messages().size() == 1
        report.messages().get(0).code == DefaultMessages.INVALID_PORT_RANGE
        report.messages().get(0).path == PORT_FIELD_PATH

        where:
        port << [-1, 65536]
    }

    def 'test failure due to provided credentials that are empty'() {
        setup:
        discoverByAddressActionArgs.put(CREDENTIALS, [(PASSWORD):'',(USERNAME): ''])
        discoverWfsAbyAddressAction.setArguments(discoverByAddressActionArgs)

        when:
        def report = discoverWfsAbyAddressAction.process()

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
            return new ReportWithResult().argumentMessage(new ErrorMessage("code", path))
        }
        return Mock(ReportWithResult) {
            argumentMessages() >> []
            get() >> Mock(clazz) {
                path() >> path
                credentials() >> Mock(CredentialsField)
            }
        }
    }
}
