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
package org.codice.ddf.admin.api.validation;

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInvalidFieldMsg;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createMissingRequiredFieldMsg;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.codice.ddf.admin.api.config.Configuration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;

public class ValidationUtils {
    public static final String SERVICE_PID_KEY = "service.pid";

    public static final String FACTORY_PID_KEY = "service.factoryPid";

    private static final Pattern HOST_NAME_PATTERN = Pattern.compile("[0-9a-zA-Z.-]+");

    private static final Predicate<String> WHITE_SPACE = Pattern.compile("\\s")
            .asPredicate();

    private static final Predicate<String> NOT_EMPTY_NO_WHITE_SPACE =
            ((Predicate<String>) StringUtils::isEmpty).or(WHITE_SPACE);

    private static final UriPathValidator PATH_VALIDATOR = new UriPathValidator();

    public static List<ConfigurationMessage> validateString(String strToCheck, String configId) {
        List<ConfigurationMessage> errors = new ArrayList<>();
        if (StringUtils.isBlank(strToCheck)) {
            errors.add(createMissingRequiredFieldMsg(configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateStringNoWhiteSpace(String strToCheck, String configId) {
        List<ConfigurationMessage> errors = validateString(strToCheck, configId);
        if (errors.isEmpty()) {
            if (WHITE_SPACE.test(strToCheck)) {
                errors.add(createInvalidFieldMsg("Invalid whitespace in input.", configId));
            }
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateHostName(String hostName, String configId) {
        List<ConfigurationMessage> errors = validateString(hostName, configId);
        if (errors.isEmpty() && !validHostnameFormat(hostName)) {
            errors.add(createInvalidFieldMsg("Hostname format is invalid.", configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validatePort(int port, String configId) {
        List<ConfigurationMessage> errors = new ArrayList<>();
        if (!validPortFormat(port)) {
            errors.add(createInvalidFieldMsg("Port is not in valid range.", configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateContextPath(String contextPath,
            String configId) {
        List<ConfigurationMessage> errors = validateString(contextPath, configId);
        if (errors.isEmpty() && !PATH_VALIDATOR.isValidPath(contextPath)) {
            errors.add(createInvalidFieldMsg("Improperly formatted context path.", configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateUrl(String url, String configId) {
        List<ConfigurationMessage> errors = validateString(url, configId);
        if (errors.isEmpty() && !validUrlFormat(url)) {
            errors.add(createInvalidFieldMsg("Endpoint URL is not in a valid format.", configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateContextPaths(List<String> contexts,
            String configId) {
        List<ConfigurationMessage> errors = new ArrayList<>();
        if (contexts == null || contexts.isEmpty()) {
            errors.add(createMissingRequiredFieldMsg(configId));
        } else {
            errors.addAll(contexts.stream()
                    .map(context -> validateContextPath(context, configId))
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateMapping(Map<String, String> mapping,
            String configId) {
        return validateMapping(mapping, configId, StringUtils::isEmpty);
    }

    public static List<ConfigurationMessage> validateMappingNoWhiteSpace(
            Map<String, String> mapping, String configId) {
        return validateMapping(mapping, configId, NOT_EMPTY_NO_WHITE_SPACE);
    }

    public static <T extends Configuration> List<ConfigurationMessage> validate(List<String> fields,
            T configuration,
            Map<String, Function<T, List<ConfigurationMessage>>> fieldsToValidations) {
        return fields.stream()
                .map(s -> fieldsToValidations.get(s)
                        .apply(configuration))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static class UriPathValidator extends UrlValidator {
        @Override
        protected boolean isValidPath(String path) {
            return super.isValidPath(path);
        }
    }

    private static boolean validUrlFormat(String uriStr) {
        try {
            new URI(uriStr);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static boolean validHostnameFormat(String hostname) {
        return HOST_NAME_PATTERN.matcher(hostname)
                .matches();
    }

    private static boolean validPortFormat(int port) {
        return port > 0 && port < 65536;
    }

    private static List<ConfigurationMessage> validateMapping(Map<String, String> mapping,
            String configId, Predicate<String> predicate) {
        List<ConfigurationMessage> errors = new ArrayList<>();
        if (mapping == null || mapping.isEmpty()) {
            errors.add(createMissingRequiredFieldMsg(configId));
        } else if (mapping.values()
                .stream()
                .anyMatch(predicate)) {
            errors.add(createInvalidFieldMsg("Map value is invalid.", configId));
        }

        return errors;
    }
}
