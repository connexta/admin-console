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
 */
package org.codice.ddf.admin.api.handler;

import java.util.List;
import java.util.Optional;

import org.codice.ddf.admin.api.config.Configuration;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.CapabilitiesReport;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.api.handler.report.Report;

/**
 * <b> This code is experimental. While this class is functional and tested, it may change or be
 * removed in a future version of the library. </b>
 * <p>
 * The {@link DefaultConfigurationHandler} will find and invoke a {@link org.codice.ddf.admin.api.handler.method.ConfigurationHandlerMethod} if one exists,
 * otherwise it will return a no method found {@link Report}.
 */
public abstract class DefaultConfigurationHandler<S extends Configuration>
        implements ConfigurationHandler<S> {

    /**
     * Return all the {@link ProbeMethod} this {@link ConfigurationHandler} supports.
     *
     * @return a {@link List} of {@link ProbeMethod}
     */
    public abstract List<ProbeMethod> getProbeMethods();

    /**
     * Return all the {@link TestMethod} this {@link ConfigurationHandler} supports.
     *
     * @return a {@link List} of {@link TestMethod}
     */
    public abstract List<TestMethod> getTestMethods();

    /**
     * Return all the {@link PersistMethod} this {@link ConfigurationHandler} supports.
     *
     * @return a {@link List} of {@link PersistMethod}
     */
    public abstract List<PersistMethod> getPersistMethods();

    @Override
    public ProbeReport probe(String probeId, S configuration) {
        if (getProbeMethods() == null) {
            return getNoProbeFoundReport(probeId);
        }

        Optional<ProbeMethod> matchedProbeMethod = getProbeMethods().stream()
                .filter(method -> method.id()
                        .equals(probeId))
                .findFirst();
        if (!matchedProbeMethod.isPresent()) {
            return getNoProbeFoundReport(probeId);
        }

        ProbeReport validationReport = new ProbeReport();
        validationReport.addMessages(matchedProbeMethod.get()
                .validate(configuration));
        if (validationReport.containsFailureMessages()) {
            return validationReport;
        }

        return matchedProbeMethod.get()
                .probe(configuration);
    }

    @Override
    public Report test(String testId, S configuration) {
        if (getTestMethods() == null) {
            return getNoTestFoundReport(testId);
        }

        Optional<TestMethod> testMethod = getTestMethods().stream()
                .filter(method -> method.id()
                        .equals(testId))
                .findFirst();

        if (!testMethod.isPresent()) {
            return getNoTestFoundReport(testId);
        }

        Report validationReport = new Report();
        validationReport.addMessages(testMethod.get()
                .validate(configuration));
        if (validationReport.containsFailureMessages()) {
            return validationReport;
        }

        return testMethod.get()
                .test(configuration);
    }

    @Override
    public Report persist(String persistId, S configuration) {
        if (getPersistMethods() == null) {
            return getNoTestFoundReport(persistId);
        }

        Optional<PersistMethod> persistMethod = getPersistMethods().stream()
                .filter(method -> method.id()
                        .equals(persistId))
                .findFirst();

        if (!persistMethod.isPresent()) {
            return getNoTestFoundReport(persistId);
        }

        Report validationReport = new Report();
        validationReport.addMessages(persistMethod.get()
                .validate(configuration));
        if (validationReport.containsFailureMessages()) {
            return validationReport;
        }

        return persistMethod.get()
                .persist(configuration);
    }

    private Report getNoTestFoundReport(String badId) {
        return new Report(ConfigurationMessage.buildMessage(ConfigurationMessage.MessageType.FAILURE,
                ConfigurationMessage.NO_METHOD_FOUND,
                "Unknown method id: \"" + (badId == null ? "null" : badId + "\".")));
    }

    private ProbeReport getNoProbeFoundReport(String badId) {
        return new ProbeReport(ConfigurationMessage.buildMessage(ConfigurationMessage.MessageType.FAILURE,
                ConfigurationMessage.NO_METHOD_FOUND,
                "Unknown probe id \"" + (badId == null ? "null" : badId) + "\"."));
    }

    @Override
    public CapabilitiesReport getCapabilities() {
        return new CapabilitiesReport(getConfigurationType().configTypeName(),
                getConfigurationHandlerId(),
                getTestMethods(),
                getProbeMethods(),
                getPersistMethods());
    }
}
