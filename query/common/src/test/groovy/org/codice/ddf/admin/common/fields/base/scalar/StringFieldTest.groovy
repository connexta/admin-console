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
package org.codice.ddf.admin.common.fields.base.scalar

import org.codice.ddf.admin.common.report.message.DefaultMessages;
import spock.lang.Specification;

class StringFieldTest extends Specification {

    def 'Empty field error when empty string provided'() {
        setup:
        def stringField = new StringField()
        stringField.setValue('')

        when:
        def validationMsgs = stringField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs[0].getCode() == DefaultMessages.EMPTY_FIELD
        validationMsgs[0].getPath() == [StringField.DEFAULT_FIELD_NAME]
    }
}
