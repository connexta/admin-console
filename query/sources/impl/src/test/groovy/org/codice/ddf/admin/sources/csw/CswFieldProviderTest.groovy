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
package org.codice.ddf.admin.sources.csw

import org.codice.ddf.admin.api.FieldProvider
import spock.lang.Ignore
import spock.lang.Specification

class CswFieldProviderTest extends Specification {

    CswFieldProvider cswFieldProvider

    def setup() {
        cswFieldProvider = new CswFieldProvider()
    }

    @Ignore
    def 'Verify discovery fields immutability'() {
        when:
        cswFieldProvider.getDiscoveryFields().add(Mock(FieldProvider))

        then:
        thrown(UnsupportedOperationException)
    }

    @Ignore
    def 'Verify persist functions immutability'() {
        when:
        cswFieldProvider.getMutationFunctions().add(Mock(FieldProvider))

        then:
        thrown(UnsupportedOperationException)
    }
}
