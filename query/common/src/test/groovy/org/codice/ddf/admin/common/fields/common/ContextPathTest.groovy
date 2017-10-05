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

    static FIELD_PATH = [ContextPath.DEFAULT_FIELD_NAME]

    ContextPath contextPath

    def setup() {
        contextPath = new ContextPath()
        contextPath.setPath(FIELD_PATH)
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
        '/..'          | FIELD_PATH | DefaultMessages.INVALID_CONTEXT_PATH
        '/../'         | FIELD_PATH | DefaultMessages.INVALID_CONTEXT_PATH
        '/..//file'    | FIELD_PATH | DefaultMessages.INVALID_CONTEXT_PATH
        '/test1//file' | FIELD_PATH | DefaultMessages.INVALID_CONTEXT_PATH
    }

    def 'Empty field error when context path not provided'() {
        setup:
        contextPath.setValue('')

        when:
        def validationMsgs = contextPath.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.EMPTY_FIELD
        validationMsgs.get(0).getPath() == FIELD_PATH
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        ContextPath emptyContextPath = new ContextPath()
        emptyContextPath.setValue('')

        ContextPath missingContextPath = new ContextPath().isRequired(true)

        ContextPath invalidContextPath = new ContextPath()
        invalidContextPath.setValue('/..')

        when:
        def errorCodes = contextPath.getErrorCodes()
        def emptyContextPathValidation = emptyContextPath.validate()
        def missingContextPathValidation = missingContextPath.validate()
        def invalidContextPathValidation = invalidContextPath.validate()

        then:
        errorCodes.size() == 3
        errorCodes.contains(emptyContextPathValidation.get(0).getCode())
        errorCodes.contains(missingContextPathValidation.get(0).getCode())
        errorCodes.contains(invalidContextPathValidation.get(0).getCode())
    }
}
