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

package org.codice.ddf.admin.api.config.context;

import static org.codice.ddf.admin.api.validation.SecurityValidationUtils.validateAuthTypes;
import static org.codice.ddf.admin.api.validation.SecurityValidationUtils.validateRealm;
import static org.codice.ddf.admin.api.validation.ValidationUtils.validateContextPaths;
import static org.codice.ddf.admin.api.validation.ValidationUtils.validateMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.validation.SecurityValidationUtils;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

/**
 * A {@link ContextPolicyBin} represents a context policy to be applied to a set of context paths.
 */
public class ContextPolicyBin {

    public static final String CONTEXT_PATHS = "contextPaths";

    public static final String REALM = "realm";

    public static final String AUTH_TYPES = "authenticationTypes";

    public static final String REQ_ATTRIS = "requiredAttributes";

    private Set<String> contextPaths;

    private String realm;

    private Set<String> authenticationTypes;

    private Map<String, String> requiredAttributes;

    private static final Map<String, Function<ContextPolicyBin, List<ConfigurationMessage>>>
            FIELD_TO_VALIDATION_FUNC =
            new ImmutableMap.Builder<String, Function<ContextPolicyBin, List<ConfigurationMessage>>>().put(
                    REALM,
                    config -> validateRealm(config.realm(), REALM))
                    .put(CONTEXT_PATHS,
                            config -> validateContextPaths(new ArrayList<>(config.contextPaths()),
                                    CONTEXT_PATHS))
                    .put(AUTH_TYPES,
                            config -> validateAuthTypes(config.authenticationTypes(), AUTH_TYPES))
                    .put(REQ_ATTRIS,
                            config -> validateMapping(config.requiredAttributes(), REQ_ATTRIS))
                    .build();

    public ContextPolicyBin() {
        authenticationTypes = new HashSet<>();
        requiredAttributes = new HashMap<>();
        contextPaths = new HashSet<>();
    }

    public List<ConfigurationMessage> validate(List<String> fields) {
        return fields.stream()
                .map(s -> FIELD_TO_VALIDATION_FUNC.get(s)
                        .apply(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    //Getters
    public Set<String> contextPaths() {
        return contextPaths;
    }

    public Set<String> authenticationTypes() {
        return authenticationTypes;
    }

    public Map<String, String> requiredAttributes() {
        return requiredAttributes;
    }

    public String realm() {
        return realm;
    }

    //Setters
    public ContextPolicyBin realm(String realm) {
        // Ensure that the realm has been converted to a correct, expected case
        this.realm = SecurityValidationUtils.normalizeRealm(realm);
        return this;
    }

    public ContextPolicyBin authenticationTypes(Set<String> authenticationTypes) {
        // Ensure that the authentication type names have been converted to correct, expected case
        this.authenticationTypes = authenticationTypes.stream()
                .map(SecurityValidationUtils::normalizeAuthType)
                .collect(Collectors.toSet());
        return this;
    }

    public ContextPolicyBin requiredAttributes(Map<String, String> requiredAttributes) {
        this.requiredAttributes = requiredAttributes;
        return this;
    }

    public ContextPolicyBin contextPaths(Set<String> contextPaths) {
        this.contextPaths = contextPaths;
        return this;
    }

    public ContextPolicyBin contextPaths(String context) {
        contextPaths.add(context);
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("contextPaths", contextPaths)
                .add("realm", realm)
                .add("authenticationTypes", authenticationTypes)
                .add("requiredAttributes", requiredAttributes)
                .toString();
    }
}
