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

import org.codice.ddf.admin.api.fields.ObjectField
import org.codice.ddf.admin.common.fields.TestObjectField
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import spock.lang.Specification

class BaseFieldTest extends Specification {

    BaseObjectField field

    def setup() {
       field = new TestObjectField()
    }

    def 'test field paths of nested ObjectFields is correct order'() {
        when:
        def field1 = field.getFields().get(0)
        def field2 = ((ObjectField) field1).getFields().get(0)

        then:
        field.path() == [TestObjectField.DEFAULT_FIELD_NAME]
        field1.path() == [TestObjectField.DEFAULT_FIELD_NAME, TestObjectField.InnerTestObjectField.DEFAULT_FIELD_NAME]
        field2.path() == [TestObjectField.DEFAULT_FIELD_NAME, TestObjectField.InnerTestObjectField.DEFAULT_FIELD_NAME, StringField.DEFAULT_FIELD_NAME]
    }
}
