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

package org.codice.ddf.admin.api.handler.report;

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.handler.ConfigurationMessage;

import com.google.common.collect.Multimap;

/**
 * A {@link Report} is used to relay the results to the caller of an operation performed on a {@link org.codice.ddf.admin.api.config.Configuration}
 * by the {@link org.codice.ddf.admin.api.handler.ConfigurationHandler}.
 */
public class Report {

    List<ConfigurationMessage> messages;

    public Report() {
        this.messages = new ArrayList<>();
    }

    public Report(List<ConfigurationMessage> messages) {
        this();
        addMessages(messages);
    }

    public Report(ConfigurationMessage... messages) {
        this();
        if (messages != null) {
            addMessages(Arrays.asList(messages));
        }
    }

    public boolean containsUnsuccessfulMessages() {
        return messages.stream()
                .anyMatch(msg -> msg.type() != ConfigurationMessage.MessageType.SUCCESS);
    }

    public boolean containsFailureMessages() {
        return messages.stream()
                .anyMatch(msg -> msg.type() == ConfigurationMessage.MessageType.FAILURE);
    }

    public static Report createReport(Map<String, String> successTypes,
            Map<String, String> failureTypes, Map<String, String> warningTypes, String result) {
        return createReport(successTypes, failureTypes, warningTypes,
                Collections.singletonList(result));
    }

    public static Report createReport(Map<String, String> successTypes,
            Map<String, String> failureTypes, Map<String, String> warningTypes,
            List<String> results) {
        return new Report().addMessages(results.stream()
                .map(result -> buildMessage(successTypes, failureTypes, warningTypes, result))
                .collect(Collectors.toList()));
    }

    public static Report createReport(Map<String, String> successTypes,
            Map<String, String> failureTypes, Map<String, String> warningTypes,
            Multimap<String, String> resultsToConfigIds) {
        return new Report().addMessages(resultsToConfigIds.entries()
                .stream()
                .map(entry -> buildMessage(successTypes,
                        failureTypes,
                        warningTypes,
                        entry.getKey(),
                        entry.getValue()))
                .collect(Collectors.toList()));
    }

    //Getters
    public List<ConfigurationMessage> messages() {
        return messages;
    }

    //Setters
    public Report addMessage(ConfigurationMessage message) {
        if (message != null) {
            this.messages.add(message);
        }
        return this;
    }

    public Report addMessages(List<ConfigurationMessage> messages) {
        if (messages != null && !messages.isEmpty()) {
            messages.stream()
                    .filter(Objects::nonNull)
                    .forEach(msg -> this.messages.add(msg));
        }
        return this;
    }
}
