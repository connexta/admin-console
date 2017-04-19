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

    BaseObjectField topLevelField

    def setup() {
        topLevelField = new TestObjectField()
    }

    def 'test field paths of nested ObjectFields is correct order'() {
        when:
        def innerField = topLevelField.getFields().get(0)
        def subFieldOfInnerField = ((ObjectField) innerField).getFields().get(0)

        List<String> topLevelFieldPath = topLevelField.path()
        List<String> innerFieldPath = innerField.path()
        List<String> subFieldOfInnerFieldPath = subFieldOfInnerField.path()

        then:
        topLevelFieldPath == [TestObjectField.DEFAULT_FIELD_NAME]
        innerFieldPath == [topLevelFieldPath, TestObjectField.InnerTestObjectField.DEFAULT_FIELD_NAME].flatten()
        subFieldOfInnerFieldPath == [innerFieldPath, StringField.DEFAULT_FIELD_NAME].flatten()
    }
}
