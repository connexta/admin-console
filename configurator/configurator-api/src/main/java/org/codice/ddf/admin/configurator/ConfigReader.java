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
package org.codice.ddf.admin.configurator;

import java.nio.file.Path;
import java.util.Map;

/**
 * Provides reader methods to system configuration items.
 * <p>
 * <b> This code is experimental. While this class is functional and tested, it may change or be
 * removed in a future version of the library. </b>
 */
public interface ConfigReader {
    /**
     * Determines if the bundle with the given name is started.
     *
     * @param bundleSymName the symbolic name of the bundle
     * @return true if started; else, false
     */
    boolean isBundleStarted(String bundleSymName);

    /**
     * Determines if the feature with the given name is started.
     *
     * @param featureName the name of the feature
     * @return true if started; else, false
     */
    boolean isFeatureStarted(String featureName);

    /**
     * Gets the current key:value pairs set in the given property file.
     *
     * @param propFile the property file to query
     * @return the current set of key:value pairs
     */
    Map<String, String> getProperties(Path propFile);

    /**
     * Gets the current key:value pairs set in the given configuration file.
     *
     * @param configPid the configId of the bundle configuration file to query
     * @return the current set of key:value pairs
     */
    Map<String, Object> getConfig(String configPid);

    /**
     * For the given managed service factory, retrieves the full complement of configuration properties.
     * <p>
     * This will get all the key:value pairs for each available configuration.
     *
     * @param factoryPid the factoryPid of the service to query
     * @return the the current sets of key:value pairs, in a map keyed on {@code configId}
     */
    Map<String, Map<String, Object>> getManagedServiceConfigs(String factoryPid);

    /**
     * Retrieves the service reference. The reference should only be used for reading purposes,
     * any changes should be done through a commit
     *
     * @param <S>          Service interface
     * @param serviceClass - Class of service to retrieve
     * @return first found service reference of serviceClass
     * @throws ConfiguratorException if any errors occur
     */
    <S> S getServiceReference(Class<S> serviceClass) throws ConfiguratorException;
}
