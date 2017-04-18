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
package org.codice.ddf.admin.common.fields.base

import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.api.fields.ObjectField
import org.codice.ddf.admin.common.fields.TestObjectField
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import spock.lang.Specification

class ListFieldImplTest extends Specification {

    static final String FIELD_NAME = "testFieldName"

    static final String LIST_FIELD_NAME = "listFieldName"

    ListFieldImpl listField

    def setup() {

    }

    def 'test the path of fields in listfields are the listfields path + their own'() {
        when:
        listField = new ListFieldImpl<>(LIST_FIELD_NAME, StringField.class)
        def field = new StringField(FIELD_NAME)
        listField.setValue(Collections.singletonList(field))

        then:
        listField.path() == [LIST_FIELD_NAME]
        ((Field) listField.getList().get(0)).path() == [LIST_FIELD_NAME, StringField.DEFAULT_FIELD_NAME]
    }

    def 'test the path of ObjectFields and their inner fields in listfields'() {
        when:
        listField = new ListFieldImpl<>(LIST_FIELD_NAME, TestObjectField.class)
        def field = new TestObjectField()
        listField.setValue(Collections.singletonList(field.getValue()))

        then:
        listField.path() == [LIST_FIELD_NAME]
        ((Field) listField.getList().get(0)).path() == [LIST_FIELD_NAME, TestObjectField.DEFAULT_FIELD_NAME]
        ((BaseObjectField) listField.getList().get(0)).getFields().get(0).path() == [LIST_FIELD_NAME, TestObjectField.DEFAULT_FIELD_NAME, TestObjectField.InnerTestObjectField.DEFAULT_FIELD_NAME]
        ((BaseObjectField)((BaseObjectField) listField.getList().get(0)).getFields().get(0)).getFields().get(0).path() == [LIST_FIELD_NAME, TestObjectField.DEFAULT_FIELD_NAME, TestObjectField.InnerTestObjectField.DEFAULT_FIELD_NAME, StringField.DEFAULT_FIELD_NAME]
    }
}
