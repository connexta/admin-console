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
package org.codice.ddf.admin.sources.opensearch

import org.codice.ddf.admin.api.action.Action
import spock.lang.Specification

class OpenSearchActionCreatorTest extends Specification {

    OpenSearchActionCreator openSearchActionCreator

    def setup() {
        openSearchActionCreator = new OpenSearchActionCreator()
    }

    def 'Verify discovery actions immutability'() {
        when:
        openSearchActionCreator.getDiscoveryActions().add(Mock(Action))

        then:
        thrown(UnsupportedOperationException)
    }

    def 'Verify persist actions immutability'() {
        when:
        openSearchActionCreator.getPersistActions().add(Mock(Action))

        then:
        thrown(UnsupportedOperationException)
    }
}
