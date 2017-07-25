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
package org.codice.ddf.admin.beta.mocks;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class MockServiceReader implements ServiceReader {

    private BundleContext getBundleContext() {
        return FrameworkUtil.getBundle(MockServiceReader.class)
                .getBundleContext();
    }

    @Override
    public <S> S getServiceReference(Class<S> serviceClass) throws ConfiguratorException {
        return getServices(serviceClass, null).stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public <S> Set<S> getServices(Class<S> serviceClass, String filter)
            throws ConfiguratorException {
        try {
            return getBundleContext().getServiceReferences(serviceClass, filter)
                    .stream()
                    .map(ref -> getBundleContext().getService(ref))
                    .collect(Collectors.toSet());

        } catch (InvalidSyntaxException e) {
            return Collections.emptySet();
        }
    }

}
