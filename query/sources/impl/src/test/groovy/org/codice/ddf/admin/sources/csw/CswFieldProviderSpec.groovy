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
import org.codice.ddf.admin.configurator.ConfiguratorFactory
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import spock.lang.Specification

class CswFieldProviderSpec extends Specification {

    CswFieldProvider cswFieldProvider

    def setup() {
        cswFieldProvider = new CswFieldProvider(Mock(ConfiguratorFactory),
                Mock(ServiceActions),
                Mock(ManagedServiceActions),
                Mock(ServiceReader),
                Mock(FeatureActions))
    }

    def 'Verify discovery fields immutability'() {
        when:
        cswFieldProvider.getDiscoveryFields().add(Mock(FieldProvider))

        then:
        thrown(UnsupportedOperationException)
    }

    def 'Verify persist functions immutability'() {
        when:
        cswFieldProvider.getMutationFunctions().add(Mock(FieldProvider))

        then:
        thrown(UnsupportedOperationException)
    }
}
