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

package org.codice.admin.router;

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInvalidFieldMsg;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.config.Configuration;
import org.codice.ddf.admin.api.handler.ConfigurationHandler;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.api.handler.report.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;

public class ConfigurationHandlerRouter implements SparkApplication {

    public static final String UNKNOWN_ERROR_MSG = "Unknown error occurred while handling request.";

    public static final String CONFIGURATION_TYPE_FIELD = "configurationType";

    public static final String APPLICATION_JSON = "application/json";

    public static final String CONFIG_HANDLER_ID = "configHanderId";

    public static final String TEST_ID = "testId";

    public static final String PERSIST_ID = "persistId";

    public static final String PROBE_ID = "probeId";

    public static final String NO_CONFIG_ID_MSG =
            "No configuration handler with id of \"%s\" found.";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationHandlerRouter.class);

    private List<ConfigurationHandler> handlers = new ArrayList<>();

    @Override
    public void init() {

        post(String.format("/test/:%s/:%s", CONFIG_HANDLER_ID, TEST_ID), this::test, this::toJson);

        post(String.format("/persist/:%s/:%s", CONFIG_HANDLER_ID, PERSIST_ID),
                this::persist,
                this::toJson);

        post(String.format("/probe/:%s/:%s", CONFIG_HANDLER_ID, PROBE_ID),
                this::probe,
                this::toJson);

        get(String.format("/configurations/:%s", CONFIG_HANDLER_ID),
                this::configurations,
                this::toJson);

        get("/capabilities", (req, res) -> getCapabilities(), this::toFilteredJson);

        get(String.format("/capabilities/:%s", CONFIG_HANDLER_ID),
                this::configCapabilities,
                this::toFilteredJson);

        after("/*", (req, res) -> res.type(APPLICATION_JSON));

        exception(Exception.class, (ex, req, res) -> {
            LOGGER.error("Configuration Handler router error: ", ex);
            res.status(500);
            res.type(APPLICATION_JSON);
            res.body(exToJSON(ex));
        });
    }

    protected Report test(Request req, Response res) throws ConfigurationHandlerRouterException {
        Report testReport = new Report();

        String configHandlerId = req.params(CONFIG_HANDLER_ID);
        String testId = req.params(TEST_ID);

        ConfigurationHandler configHandler = getConfigurationHandler(configHandlerId);
        if (configHandler == null) {
            res.status(400);
            return testReport.addMessage(createInvalidFieldMsg(getNoConfigMsg(configHandlerId),
                    CONFIGURATION_TYPE_FIELD));
        }

        try {
            Configuration config = getGsonParser().fromJson(req.body(), Configuration.class);
            testReport = configHandler.test(testId, config);

            if (testReport.containsUnsuccessfulMessages()) {
                res.status(400);
            }

            return testReport;
        } catch (Throwable t) {
            throw new ConfigurationHandlerRouterException(UNKNOWN_ERROR_MSG, t);
        }
    }

    protected Report persist(Request req, Response res) throws ConfigurationHandlerRouterException {
        Report persistReport = new Report();

        String configHandlerId = req.params(CONFIG_HANDLER_ID);
        String persistId = req.params(PERSIST_ID);

        ConfigurationHandler configHandler = getConfigurationHandler(configHandlerId);
        if (configHandler == null) {
            res.status(400);
            return persistReport.addMessage(createInvalidFieldMsg(getNoConfigMsg(configHandlerId),
                    CONFIGURATION_TYPE_FIELD));
        }

        try {
            Configuration config = getGsonParser().fromJson(req.body(), Configuration.class);
            persistReport = configHandler.persist(persistId, config);

            if (persistReport.containsUnsuccessfulMessages()) {
                res.status(400);
            }

            return persistReport;
        } catch (Throwable t) {
            throw new ConfigurationHandlerRouterException(UNKNOWN_ERROR_MSG, t);
        }
    }

    protected Report probe(Request req, Response res) throws ConfigurationHandlerRouterException {
        Report probeReport = new ProbeReport();

        String configHandlerId = req.params(CONFIG_HANDLER_ID);
        String probeId = req.params(PROBE_ID);

        ConfigurationHandler configHandler = getConfigurationHandler(configHandlerId);
        if (configHandler == null) {
            res.status(400);
            return probeReport.addMessage(createInvalidFieldMsg(getNoConfigMsg(configHandlerId),
                    CONFIGURATION_TYPE_FIELD));
        }

        try {
            Configuration config = getGsonParser().fromJson(req.body(), Configuration.class);
            probeReport = configHandler.probe(probeId, config);

            if (probeReport.containsUnsuccessfulMessages()) {
                res.status(400);
            }

            return probeReport;
        } catch (Throwable t) {
            throw new ConfigurationHandlerRouterException(UNKNOWN_ERROR_MSG, t);
        }
    }

    protected Object getCapabilities() {
        return handlers.stream()
                .map(ConfigurationHandler::getCapabilities)
                .collect(Collectors.toList());
    }

    protected Object configCapabilities(Request req, Response res) {
        String configHandlerId = req.params(CONFIG_HANDLER_ID);
        ConfigurationHandler configHandler = getConfigurationHandler(configHandlerId);
        if (configHandler == null) {
            res.status(400);
            return new Report(createInvalidFieldMsg(getNoConfigMsg(configHandlerId),
                    CONFIGURATION_TYPE_FIELD));
        }
        return configHandler.getCapabilities();
    }

    protected Object configurations(Request req, Response res) {
        String configHandlerId = req.params(CONFIG_HANDLER_ID);
        ConfigurationHandler configHandler = getConfigurationHandler(configHandlerId);
        if (configHandler == null) {
            res.status(400);
            return new Report(createInvalidFieldMsg(getNoConfigMsg(configHandlerId),
                    CONFIGURATION_TYPE_FIELD));
        }
        return configHandler.getConfigurations();
    }

    private String exToJSON(Exception ex) {
        Map<String, Object> e = new HashMap<>();
        e.put("stackTrace", ex.getStackTrace());
        e.put("cause", ex.toString());
        return new Gson().toJson(e);
    }

    public ConfigurationHandler getConfigurationHandler(String configurationId) {
        return handlers.stream()
                .filter(handler -> handler.getConfigurationHandlerId()
                        .equals(configurationId))
                .findFirst()
                .orElse(null);
    }

    private Gson getGsonParser() {
        RuntimeTypeAdapterFactory rtaf = RuntimeTypeAdapterFactory.of(Configuration.class,
                CONFIGURATION_TYPE_FIELD);
        handlers.stream()
                .map(ConfigurationHandler::getConfigurationType)
                .forEach(configType -> rtaf.registerSubtype(configType.configClass(),
                        configType.configTypeName()));

        return new GsonBuilder().registerTypeAdapterFactory(rtaf)
                .create();
    }

    private String toJson(Object body) {
        return getGsonParser().toJson(body);
    }

    private String toFilteredJson(Object body) {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create()
                .toJson(body);
    }

    public void setConfigurationHandlers(List<ConfigurationHandler> configurationHandlers) {
        handlers = configurationHandlers;
    }

    private String getNoConfigMsg(String configHandlerId) {
        return String.format(NO_CONFIG_ID_MSG, configHandlerId);
    }
}
