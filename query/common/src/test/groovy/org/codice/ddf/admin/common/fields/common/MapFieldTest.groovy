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

import org.codice.ddf.admin.api.report.ErrorMessage
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class MapFieldTest extends Specification {

    List<Object> MAP_FIELD_PATH = [MapField.DEFAULT_FIELD_NAME]

    MapField mapField

    static ENTRIES = PairField.ListImpl.DEFAULT_FIELD_NAME

    def setup() {
        mapField = new MapField()
        mapField.setPath(MAP_FIELD_PATH)
    }

    def 'Putting the same key twice overrides the key\'s original value'() {
        when:
        mapField.put('key', 'value1')

        then:
        mapField.getValue().get(ENTRIES).get(0).get('value') == 'value1'
        mapField.containsValue('value1')

        when:
        mapField.put('key', 'value2')

        then:
        mapField.getValue().get(ENTRIES).get(0).get('value') == 'value2'
        !mapField.containsValue('value1')
        mapField.containsValue('value2')
    }

    def 'Contains key and value'() {
        when:
        mapField.put('key1', 'value1')

        then:
        mapField.containsKey('key1')
        mapField.containsValue('value1')
    }

    def 'Does not contain key or value'() {
        when:
        mapField.put('key1', 'value1')

        then:
        !mapField.containsKey('notMyKey')
        !mapField.containsValue('notMyValue')
    }

    def 'Fail validation due to duplicate keys'() {
        setup:
        def value = [(ENTRIES): [
                createEntry('key1', 'value1'),
                createEntry('key2', 'value2'),
                createEntry('key1', 'value3')
        ]]
        mapField.setValue(value)
        mapField.setPath(MAP_FIELD_PATH)

        when:
        List<ErrorMessage> validationMsgs = mapField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.DUPLICATE_MAP_KEY
        validationMsgs.get(0).getPath() == [MapField.DEFAULT_FIELD_NAME, ENTRIES, 2]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        def duplicateValue = [(ENTRIES): [createEntry('key1', 'value1'),
                                          createEntry('key1', 'value2')]]
        MapField duplicateValueMapField = new MapField()
        duplicateValueMapField.setValue(duplicateValue)
        duplicateValueMapField.setPath(MAP_FIELD_PATH)

        def emptyValue = [(ENTRIES): [createEntry('', '')]]
        MapField emptyValueMapField = new MapField()
        emptyValueMapField.setValue(emptyValue)
        emptyValueMapField.setPath(MAP_FIELD_PATH)

        def missingValue = [(ENTRIES): [createEntry('', null)]]
        MapField missingValueMapField = new MapField().isRequired(true)
        missingValueMapField.setValue(missingValue)
        missingValueMapField.setPath(MAP_FIELD_PATH)

        when:
        def errorCodes = mapField.getErrorCodes()
        def duplicateValueValidation = duplicateValueMapField.validate()
        def emptyValueValidation = emptyValueMapField.validate()
        def missingValueValidation = missingValueMapField.validate()

        then:
        errorCodes.size() == 3
        errorCodes.contains(duplicateValueValidation.get(0).getCode())
        errorCodes.contains(emptyValueValidation.get(0).getCode())
        errorCodes.contains(missingValueValidation.get(0).getCode())
    }

    def createEntry(String key, String value) {
        return [(PairField.KEY_FIELD_NAME): key, (PairField.VALUE_FIELD_NAME): value]
    }
}
