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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class MessageBuilder {

    private Map<String, String> successTypes;
    private Map<String, String> failureTypes;
    private Map<String, String> warningTypes;
    private Map<String, String> allTypes;

    public MessageBuilder(Map<String, String> successTypes, Map<String, String> failureTypes, Map<String, String> warningTypes){
        this.successTypes = ImmutableMap.<String, String>builder().putAll(successTypes).build();
        this.failureTypes = ImmutableMap.<String, String>builder().putAll(failureTypes).build();
        this.warningTypes = ImmutableMap.<String, String>builder().putAll(warningTypes).build();
        this.allTypes = ImmutableMap.<String, String>builder().putAll(successTypes).putAll(failureTypes).putAll(warningTypes).build();
    }

    public Map<String, String> getDescriptions(String... subtypeKeys) {
        Map<String, String> descriptions = new HashMap<>();
        Arrays.stream(subtypeKeys)
                .forEach(key -> descriptions.put(key, allTypes.get(key)));
        return descriptions;
    }
    public ConfigurationMessage buildMessage(String result) {
        return ConfigurationMessage.buildMessage(successTypes, failureTypes, warningTypes, result);
    }

    public ConfigurationMessage buildMessage(String result, String configFieldId) {
        return ConfigurationMessage.buildMessage(successTypes, failureTypes, warningTypes, result, configFieldId);
    }
}
