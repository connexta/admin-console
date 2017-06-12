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
package org.codice.ddf.admin.common.fields.common

import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class ContextPathTest extends Specification {

    ContextPath contextPath

    def setup() {
        contextPath = new ContextPath()
    }

    def 'Valid context paths'() {
        setup:
        contextPath.setValue(path)

        expect:
        contextPath.validate().isEmpty()

        where:
        path << ['/', '/test1', '/t123', '/$23', '/test1/', '/test1/file']
    }

    def 'Invalid context paths'() {
        setup:
        contextPath.setValue(path)

        when:
        def validationMsgs = contextPath.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == errorCode
        validationMsgs.get(0).getPath() == fieldPath

        where:
        path           | fieldPath                        | errorCode
        '/..'          | [ContextPath.DEFAULT_FIELD_NAME] | DefaultMessages.INVALID_CONTEXT_PATH
        '/../'         | [ContextPath.DEFAULT_FIELD_NAME] | DefaultMessages.INVALID_CONTEXT_PATH
        '/..//file'    | [ContextPath.DEFAULT_FIELD_NAME] | DefaultMessages.INVALID_CONTEXT_PATH
        '/test1//file' | [ContextPath.DEFAULT_FIELD_NAME] | DefaultMessages.INVALID_CONTEXT_PATH
    }

    def 'Empty field error when context path not provided'() {
        setup:
        contextPath.setValue('')

        when:
        def validationMsgs = contextPath.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.EMPTY_FIELD
        validationMsgs.get(0).getPath() == [ContextPath.DEFAULT_FIELD_NAME]
    }
}
