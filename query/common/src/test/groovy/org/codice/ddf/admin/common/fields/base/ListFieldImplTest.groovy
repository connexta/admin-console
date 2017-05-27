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

import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.api.fields.ObjectField
import org.codice.ddf.admin.common.fields.TestObjectField
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import spock.lang.Specification

class ListFieldImplTest extends Specification {

    static final String LIST_FIELD_NAME = "listFieldName"

    def 'test the path of fields in listfields are the listfields path + their own'() {
        when:
        ListFieldImpl<StringField> listField = new ListFieldImpl<>(LIST_FIELD_NAME, StringField.class)
        listField.add(new StringField())

        then:
        listField.path() == [LIST_FIELD_NAME]
        listField.getList().get(0).path() == [LIST_FIELD_NAME, ListField.INDEX_DELIMETER + 0]
    }

    def 'test the path of ObjectFields and their inner fields in listfields'() {
        when:
        ListFieldImpl<ObjectField> listField = new ListFieldImpl<>(LIST_FIELD_NAME, TestObjectField.class)
        listField.add(new TestObjectField())

        List<String> parentPath = listField.path()
        List<String> objectFieldPath = listField.getList().get(0).path()
        List<String> innerObjectFieldPath = listField.getList().get(0).getFields().get(0).path()
        List<String> subFieldOfInnerObjectFieldPath = ((ObjectField) listField.getList().get(0).getFields().get(0)).getFields().get(0).path()

        then:
        parentPath == [LIST_FIELD_NAME]
        objectFieldPath == [parentPath, ListField.INDEX_DELIMETER + 0].flatten()
        innerObjectFieldPath == [objectFieldPath, TestObjectField.InnerTestObjectField.DEFAULT_FIELD_NAME].flatten()
        subFieldOfInnerObjectFieldPath == [innerObjectFieldPath, StringField.DEFAULT_FIELD_NAME].flatten()
    }
}
