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

package org.codice.ddf.admin.api.handler.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.api.config.Configuration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;

import com.google.gson.annotations.Expose;

/**
 * <b> This code is experimental. While this class is functional and tested, it may change or be
 * removed in a future version of the library. </b>
 * <p>
 * A {@link ConfigurationHandlerMethod} represents a method that is meant to be performed on a {@link org.codice.ddf.admin.api.config.Configuration}.
 */
public abstract class ConfigurationHandlerMethod<S extends Configuration> {

    @Expose
    final String id;

    @Expose
    final String description;

    @Expose
    final List<String> requiredFields;

    @Expose
    final List<String> optionalFields;

    @Expose
    final Map<String, String> successTypes;

    @Expose
    final Map<String, String> failureTypes;

    @Expose
    final Map<String, String> warningTypes;

    public ConfigurationHandlerMethod(String id, String description, List<String> requiredFields,
            List<String> optionalFields, Map<String, String> successTypes,
            Map<String, String> failureTypes, Map<String, String> warningTypes) {
        this.id = id;
        this.description = description;
        this.requiredFields = requiredFields;
        this.optionalFields = optionalFields;
        this.successTypes = successTypes;
        this.failureTypes = failureTypes;
        this.warningTypes = warningTypes;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getOptionalFields() {
        return optionalFields;
    }

    public List<String> getRequiredFields() {
        return requiredFields;
    }

    public String id() {
        return id;
    }

    public List<ConfigurationMessage> validate(S configuration) {
        return Stream.concat(validateRequiredFields(configuration).stream(),
                validateOptionalFields(configuration).stream())
                .collect(Collectors.toList());
    }

    public List<ConfigurationMessage> validateRequiredFields(S configuration) {
        if (getRequiredFields() != null && !getRequiredFields().isEmpty()) {
            return configuration.validate(getRequiredFields());
        }
        return new ArrayList<>();
    }

    public List<ConfigurationMessage> validateOptionalFields(S configuration) {
        return new ArrayList<>();
    }
}
