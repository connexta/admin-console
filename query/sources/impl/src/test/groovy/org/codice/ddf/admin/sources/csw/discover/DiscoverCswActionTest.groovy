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

import org.codice.ddf.admin.api.FieldProvider
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.report.ReportWithResultImpl
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessage
import org.codice.ddf.admin.sources.commons.utils.CswSourceUtils
import org.codice.ddf.admin.sources.fields.SourceInfoField
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField
import spock.lang.Specification

import static org.codice.ddf.admin.sources.SourceTestCommons.*

class DiscoverCswActionTest extends Specification {

    FieldProvider discoverCsw

    CswSourceUtils cswSourceUtils

    static BASE_PATH = [DiscoverCswSource.ID, FunctionField.ARGUMENT]

    static ADDRESS_FIELD_PATH = [BASE_PATH, ADDRESS].flatten()

    static URL_FIELD_PATH = [ADDRESS_FIELD_PATH, URL_NAME].flatten()

    def setup() {
        cswSourceUtils = Mock(CswSourceUtils)
        discoverCsw = new DiscoverCswSource(cswSourceUtils)
    }

    def 'Successfully discover CSW configuration using URL'() {
        setup:
        discoverCsw.setArguments(getBaseDiscoverByUrlActionArgs())

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.getPreferredCswConfig(_ , _) >> createResult(false, SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'Successfully discover CSW configuration using hostname and port'() {
        setup:
        discoverCsw.setArguments(getBaseDiscoverByAddressActionArgs())

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_, _) >> createResult(false, UrlField.class)
        1 * cswSourceUtils.getPreferredCswConfig(_, _) >> createResult(false, SourceConfigUnionField.class)
        report.result() instanceof SourceInfoField
        ((SourceInfoField) report.result()).isAvailable()
        ((SourceInfoField) report.result()).config() != null
    }

    def 'Fail discovery using URL while getting preferred config'() {
        setup:
        discoverCsw.setArguments(getBaseDiscoverByUrlActionArgs())

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.getPreferredCswConfig(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'Fail when using hostname+port when discovering the URL'() {
        setup:
        discoverCsw.setArguments(getBaseDiscoverByAddressActionArgs())

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'Fail discovery using hostname+port while getting preferred config'() {
        setup:
        discoverCsw.setArguments(getBaseDiscoverByAddressActionArgs())

        when:
        def report = discoverCsw.process()

        then:
        1 * cswSourceUtils.discoverCswUrl(_, _) >> createResult(false, UrlField.class)
        1 * cswSourceUtils.getPreferredCswConfig(_, _) >> createResult(true, null)
        report.result() == null
        report.messages().size() == 1
    }

    def 'Fail when missing required fields'() {
        when:
        def report = discoverCsw.process()

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
            return Mock(ReportWithResultImpl) {
                argumentMessages() >> [new ErrorMessage("code", [])]
                resultMessages() >> []
            }
        }
        return Mock(ReportWithResultImpl) {
            argumentMessages() >> []
            resultMessages() >> []
            result() >> Mock(clazz) {
                path() >> []
                credentials() >> Mock(CredentialsField)
            }
        }
    }
}
