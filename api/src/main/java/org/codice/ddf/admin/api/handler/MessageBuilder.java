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
