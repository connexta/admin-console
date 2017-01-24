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
package org.codice.ddf.admin.api.validation

import com.google.common.collect.ImmutableMap;
import spock.lang.Specification

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.INVALID_FIELD
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.MISSING_REQUIRED_FIELD

class ValidationUtilsTest extends Specification{
    def static sampleConfigFieldId = 'sampleConfigFieldId'

    def 'test valid String validationString'() {
        when:
        def errors = ValidationUtils.validateString("validStr", sampleConfigFieldId)

        then:
        errors.size() == 0
    }

    def 'test invalid String validationString'() {
        when:
        def errors = ValidationUtils.validateString(invalidStr, sampleConfigFieldId)
        errorFieldId = errors[0].configFieldId()
        errorSubtype = errors[0].subtype()

        then:
        errors.size() == 1

        where:
        invalidStr  | errorFieldId        | errorSubtype
        ""          | sampleConfigFieldId | MISSING_REQUIRED_FIELD
        null        | sampleConfigFieldId | MISSING_REQUIRED_FIELD
    }

    def 'test valid host name validateHostName'() {
        when:
        def errors = ValidationUtils.validateHostName(invalidHostName, sampleConfigFieldId)

        then:
        errors.size() == 0

        where:
        invalidHostName | _
        "localhost"     | _
        "google.com"    | _
        "sample.gov"    | _
        "wikipedia.org" | _
    }

    def 'test invalid host name validateHostName'() {
        when:
        def errors = ValidationUtils.validateHostName(invalidHostName, sampleConfigFieldId)
        errorFieldId = errors[0].configFieldId()
        errorSubtype = errors[0].subtype()

        then:
        errors.size() == 1

        where:
        invalidHostName    | errorFieldId        | errorSubtype
        ""                 | sampleConfigFieldId | MISSING_REQUIRED_FIELD
        null               | sampleConfigFieldId | MISSING_REQUIRED_FIELD
        "abcd!.%*?"        | sampleConfigFieldId | INVALID_FIELD
        "http://localhost" | sampleConfigFieldId | INVALID_FIELD
    }

    def 'test valid port validatePort'() {
        when:
        def errors = ValidationUtils.validatePort(validPort, sampleConfigFieldId)

        then:
        errors.size() == 0

        where:
        validPort    | _
        1            | _
        1000         | _
        8993         | _
        65535        | _
    }

    def 'test invalid port validatePort'() {
        when:
        def errors = ValidationUtils.validatePort(invalidPort, sampleConfigFieldId)
        errorFieldId = errors[0].configFieldId()
        errorSubtype = errors[0].subtype()

        then:
        errors.size() == 1

        where:
        invalidPort    | errorFieldId        | errorSubtype
        -5            | sampleConfigFieldId | INVALID_FIELD
        0             | sampleConfigFieldId | INVALID_FIELD
        65536         | sampleConfigFieldId | INVALID_FIELD
        65540         | sampleConfigFieldId | INVALID_FIELD
    }

    def 'test valid context path validateContextPath'() {
        when:
        def errors = ValidationUtils.validateContextPath(validContextPath, sampleConfigFieldId)

        then:
        errors.size() == 0

        where:
        validContextPath | _
        "/a"             | _
        "/a/b"           | _
        "/aa/bb/cc/dd"   | _
    }

    def 'test invalid context path validateContextPath'() {
        when:
        def errors = ValidationUtils.validateContextPath(invalidContextPath, sampleConfigFieldId)
        errorFieldId = errors[0].configFieldId()
        errorSubtype = errors[0].subtype()

        then:
        errors.size() == 1

        where:
        invalidContextPath | errorFieldId        | errorSubtype
        null               | sampleConfigFieldId | MISSING_REQUIRED_FIELD
        ""                 | sampleConfigFieldId | MISSING_REQUIRED_FIELD
        "abcd/"            | sampleConfigFieldId | INVALID_FIELD
        "/abcd`"           | sampleConfigFieldId | INVALID_FIELD
    }

    def 'test valid mapping validateMapping'() {
        when:
        def errors = ValidationUtils.validateMapping(validMapping, sampleConfigFieldId)

        then:
        errors.size() == 0

        where:
        validMapping                            | _
        ImmutableMap.of("entry", "value")       | _
        ImmutableMap.of("entry", "value",
                        "entry2", "value2")     | _
    }

    def 'test invalid mapping validateMapping'() {
        when:
        def errors = ValidationUtils.validateMapping(invalidMapping, sampleConfigFieldId)
        errorFieldId = errors[0].configFieldId()
        errorSubtype = errors[0].subtype()

        then:
        errors.size() == 1

//        TODO - add additional invalid mappings
        where:
        invalidMapping  | errorFieldId        | errorSubtype
        null            | sampleConfigFieldId | MISSING_REQUIRED_FIELD
        new HashMap<>() | sampleConfigFieldId | MISSING_REQUIRED_FIELD
    }
}
