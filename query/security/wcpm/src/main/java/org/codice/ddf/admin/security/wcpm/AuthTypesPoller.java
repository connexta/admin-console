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
package org.codice.ddf.admin.security.wcpm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.poller.BaseEnumValuePoller;
import org.codice.ddf.security.handler.api.AuthenticationHandler;

public class AuthTypesPoller extends BaseEnumValuePoller<AuthenticationHandler, String> {

    private List<AuthenticationHandler> authHandlers = Collections.emptyList();
    private Map<String, String> descriptionMap = new HashMap<>();

    @Override
    public List<EnumValue<String>> getEnumValues() {
        return authHandlers.stream()
                .map(this::authHandlerToEnumValue)
                .collect(Collectors.toList());
    }

    public EnumValue<String> authHandlerToEnumValue(AuthenticationHandler handler) {
        return new EnumValue<String>() {
            @Override
            public String getEnumTitle() {
                return handler.getAuthenticationType();
            }

            @Override
            public String getDescription() {
                return descriptionMap.get(handler.getAuthenticationType());
            }

            @Override
            public String getValue() {
                return handler.getAuthenticationType();
            }
        };
    }

    public void setAuthHandlers(List<AuthenticationHandler> authHandlers) {
        this.authHandlers = authHandlers;
    }

    public void setDescriptionMap(Map<String, String> descriptionMap) {
        Map<String, String> newMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        newMap.putAll(descriptionMap);
        this.descriptionMap = newMap;
    }
}
