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

import static org.codice.ddf.admin.api.config.context.ContextPolicyBin.AUTH_TYPES;
import static org.codice.ddf.admin.api.config.context.ContextPolicyBin.CONTEXT_PATHS;
import static org.codice.ddf.admin.api.config.context.ContextPolicyBin.REALM;
import static org.codice.ddf.admin.api.config.context.ContextPolicyBin.REQ_ATTRIS;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInvalidFieldMsg;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createMissingRequiredFieldMsg;
import static org.codice.ddf.admin.api.validation.ValidationUtils.validateString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.config.context.ContextPolicyBin;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;

import com.google.common.collect.ImmutableList;

public class SecurityValidationUtils {

    // TODO: tbatie - 1/20/17 - (Ticket) These realms eventually need to be configurable
    public static final String KARAF = "karaf";

    public static final String LDAP = "ldap";

    public static final String IDP_REALM = "IdP";

    public static final List<String> ALL_REALMS = ImmutableList.of(KARAF, LDAP, IDP_REALM);

    // TODO: tbatie - 1/20/17 - (Ticket) These auth types eventually need to be configurable
    public static final String SAML = "SAML";

    public static final String BASIC = "basic";

    public static final String IDP_AUTH = "IDP";

    public static final String PKI = "PKI";

    public static final String CAS = "CAS";

    public static final String GUEST = "GUEST";

    public static final List<String> ALL_AUTH_TYPES = ImmutableList.of(SAML,
            BASIC,
            IDP_AUTH,
            PKI,
            CAS,
            GUEST);

    private static final String INV_REALM_MSG = String.format(
            "Unknown realm [%%s]. Realm must be one of: %s",
            String.join(",", ALL_REALMS));

    private static final String INV_AUTH_MSG = String.format(
            "Unknown authentication type [%%s]. Authentication type must be one of: %s",
            String.join(",", ALL_AUTH_TYPES));

    public static List<ConfigurationMessage> validateContextPolicyBins(List<ContextPolicyBin> bins,
            String configId) {
        List<ConfigurationMessage> errors = new ArrayList<>();
        if (bins == null || bins.isEmpty()) {
            errors.add(createMissingRequiredFieldMsg(configId));
        } else {
            errors.addAll(bins.stream()
                    .map(cpb -> cpb.validate(Arrays.asList(REALM, CONTEXT_PATHS, AUTH_TYPES)))
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));

            errors.addAll(bins.stream()
                    .filter(cpb -> cpb.requiredAttributes() != null && !cpb.requiredAttributes()
                            .isEmpty())
                    .map(cpb -> cpb.validate(Collections.singletonList(REQ_ATTRIS)))
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateRealm(String realm, String configId) {
        List<ConfigurationMessage> errors = validateString(realm, configId);
        if (errors.isEmpty()) {
            // Check for match, regardless of case.
            if (ALL_REALMS.stream()
                    .noneMatch(realm::equalsIgnoreCase)) {
                errors.add(createInvalidFieldMsg(String.format(INV_REALM_MSG, realm), configId));
            }
        }

        return errors;
    }

    public static List<ConfigurationMessage> validateAuthTypes(Set<String> authTypes,
            String configId) {
        List<ConfigurationMessage> errors = new ArrayList<>();
        if (authTypes == null || authTypes.isEmpty()) {
            errors.add(createMissingRequiredFieldMsg(configId));
        } else {
            for (String authType : authTypes) {
                // Check for match, regardless of case.
                if (ALL_AUTH_TYPES.stream()
                        .noneMatch(authType::equalsIgnoreCase)) {
                    errors.add(createInvalidFieldMsg(String.format(INV_AUTH_MSG, authType),
                            configId));
                }
            }
        }

        return errors;
    }

    public static String normalizeAuthType(String input) throws IllegalArgumentException {
        return normalize(input, ALL_AUTH_TYPES);
    }

    public static String normalizeRealm(String input) throws IllegalArgumentException {
        return normalize(input, ALL_REALMS);
    }

    private static String normalize(String input, List<String> validVals)
            throws IllegalArgumentException {
        return validVals.stream()
                .filter(input::equalsIgnoreCase)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
