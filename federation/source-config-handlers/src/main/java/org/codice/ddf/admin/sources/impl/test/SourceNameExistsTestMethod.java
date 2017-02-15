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
package org.codice.ddf.admin.sources.impl.test;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_NAME;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_TEST;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.handler.SourceConfigurationHandler;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SourceNameExistsTestMethod extends TestMethod<SourceConfiguration> {

    public static final String SOURCE_NAME_EXISTS_TEST_ID = "source-name-exists";

    public static final String SOURCE_NAME_EXISTS_SUCCESS = "SOURCE_NAME_EXISTS_SUCCESS";

    public static final String SOURCE_NAME_EXISTS_FAIL = "SOURCE_NAME_EXISTS_FAIL";

    public static final String DESCRIPTION =
            "Verifies incoming configuration's source name against all existing configuration's source names.";

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_NAME);

    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(
            SOURCE_NAME_EXISTS_SUCCESS,
            "Source name does not exist.");

    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(SOURCE_NAME_EXISTS_FAIL,
            "Source name already exists.");

    private List<SourceConfigurationHandler> handlers;

    public SourceNameExistsTestMethod(List<SourceConfigurationHandler> handlers) {
        super(SOURCE_NAME_EXISTS_TEST_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        this.handlers = handlers;
    }

    @Override
    public Report test(SourceConfiguration configuration) {
        List<Report> sourceReport = handlers.stream()
                .map(handler -> handler.test(SOURCE_NAME_EXISTS_TEST_ID, configuration))
                .collect(Collectors.toList());

        List<Report> duplicateSourceNameReports = sourceReport.stream()
                .filter(Report::containsFailureMessages)
                .collect(Collectors.toList());

        if (!duplicateSourceNameReports.isEmpty()) {
            Report report = new Report();
            duplicateSourceNameReports.forEach(sourceNameReport -> report.addMessages(
                    sourceNameReport.messages()));
            return report;
        }

        return new Report(buildMessage(SUCCESS_TYPES, FAILURE_TYPES, null, SUCCESSFUL_TEST));
    }
}
