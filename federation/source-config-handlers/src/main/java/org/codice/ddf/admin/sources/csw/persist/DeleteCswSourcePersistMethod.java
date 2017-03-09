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
package org.codice.ddf.admin.sources.csw.persist;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SERVICE_PID;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.DELETE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.FAILED_DELETE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_DELETE;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.getCommonSourceSubtypeDescriptions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;

import com.google.common.collect.ImmutableList;

public class DeleteCswSourcePersistMethod extends PersistMethod<CswSourceConfiguration> {

    public static final String DELETE_CSW_SOURCE_ID = DELETE;

    public static final String DESCRIPTION =
            "Attempts to delete a CSW source with the given configuration.";

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(SERVICE_PID);

    private static final Map<String, String> SUCCESS_TYPES = getCommonSourceSubtypeDescriptions(
            SUCCESSFUL_DELETE);

    private static final Map<String, String> FAILURE_TYPES = getCommonSourceSubtypeDescriptions(
            FAILED_DELETE);

    private final ConfiguratorFactory configuratorFactory;

    public DeleteCswSourcePersistMethod(ConfiguratorFactory configuratorFactory) {
        super(DELETE_CSW_SOURCE_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public Report persist(CswSourceConfiguration configuration) {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.deleteManagedService(configuration.servicePid());
        OperationReport report = configurator.commit("CSW source deleted for servicePid: {}",
                configuration.servicePid());

        return Report.createReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                report.containsFailedResults() ? FAILED_DELETE : SUCCESSFUL_DELETE);
    }

}
