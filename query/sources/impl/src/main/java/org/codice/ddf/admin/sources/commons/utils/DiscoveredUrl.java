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
package org.codice.ddf.admin.sources.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.action.Message;

// TODO this can likely be collapsed down with other classes converting from ProbeReport
public class DiscoveredUrl {

    private List<Message> messages;

    private Map<String, Object> responseProperties;

    public DiscoveredUrl() {
        this(new ArrayList<>());
    }

    public DiscoveredUrl(Map<String, Object> responseProperties) {
        this(responseProperties, new ArrayList<>());
    }

    public DiscoveredUrl(List<Message> messages) {
        this(new HashMap<>(), messages);
    }

    public DiscoveredUrl(Map<String, Object> responseProperties, List<Message> messages) {
        this.messages = messages;
        this.responseProperties = responseProperties;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public DiscoveredUrl put(String key, Object value) {
        if (value != null) {
            responseProperties.put(key, value);
        }
        return this;
    }

    public <T> T get(String key) {
        return (T) responseProperties.get(key);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean hasErrors() {
        return !messages.stream()
                .filter(message -> message.getType() == Message.MessageType.ERROR)
                .collect(Collectors.toList())
                .isEmpty();
    }

    public void setResponseProperties(Map<String, Object> responseProperties) {
        this.responseProperties = responseProperties;
    }
}
