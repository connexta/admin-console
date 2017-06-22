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
package org.codice.ddf.admin.security.common.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.ListUtils;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;
import org.codice.ddf.security.policy.context.ContextPolicy;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.codice.ddf.security.policy.context.attributes.ContextAttributeMapping;

import com.google.common.collect.ImmutableMap;

public class PolicyManagerServiceProperties {

    // --- Policy manager props
    public static final String POLICY_MANAGER_PID =
            "org.codice.ddf.security.policy.context.impl.PolicyManager";

    public static final String AUTH_TYPES = "authenticationTypes";

    public static final String REALMS = "realms";

    public static final String REQUIRED_ATTRIBUTES = "requiredAttributes";

    public static final String WHITE_LIST_CONTEXT = "whiteListContexts";

    // ---

    public static final String ROOT_CONTEXT_PATH = "/";

    public static final String IDP_CLIENT_BUNDLE_NAME = "security-idp-client";

    public static final String IDP_SERVER_BUNDLE_NAME = "security-idp-server";

    public Map<String, Object> contextPoliciesToPolicyManagerProps(
            List<ContextPolicyBin> contextPolicies) {
        List<String> realmsProps = new ArrayList<>();
        List<String> authTypesProps = new ArrayList<>();
        List<String> reqAttrisProps = new ArrayList<>();

        for (ContextPolicyBin bin : contextPolicies) {
            bin.contexts()
                    .forEach(context -> {
                        realmsProps.add(context + "=" + bin.realm());
                        authTypesProps.add(context + "=" + String.join("|", bin.authTypes()));
                        if (bin.claimsMapping()
                                .isEmpty()) {
                            reqAttrisProps.add(context + "=");
                        } else {
                            reqAttrisProps.add(context + "={" + String.join(";",
                                    bin.claimsMapping()
                                            .entrySet()
                                            .stream()
                                            .map(entry -> entry.getKey() + "=" + entry.getValue())
                                            .collect(Collectors.toList())) + "}");
                        }
                    });
        }

        return ImmutableMap.of(AUTH_TYPES,
                authTypesProps.toArray(new String[0]),
                REALMS,
                realmsProps.toArray(new String[0]),
                REQUIRED_ATTRIBUTES,
                reqAttrisProps.toArray(new String[0]));
    }

    public ContextPolicyBin.ContextPolicies contextPolicyServiceToContextPolicyFields(
            ServiceReader serviceReader) {
        ContextPolicyManager policyManager =
                serviceReader.getServiceReference(ContextPolicyManager.class);
        List<ContextPolicyBin> policies = new ArrayList<>();

        Collection<ContextPolicy> allPolicies = policyManager.getAllContextPolicies();
        for (ContextPolicy policy : allPolicies) {
            boolean foundBin = false;
            Map<String, String> policyRequiredAttributes = policy.getAllowedAttributes()
                    .stream()
                    .collect(Collectors.toMap(ContextAttributeMapping::getAttributeName,
                            ContextAttributeMapping::getAttributeValue));

            //Check if bin containing an identical context policy exists already, if so add the context path to it
            for (ContextPolicyBin bin : policies) {
                if (bin.realm()
                        .equals(policy.getRealm()) && ListUtils.isEqualList(bin.authTypes(),
                        policy.getAuthenticationMethods()) && hasSameRequiredAttributes(bin,
                        policyRequiredAttributes)) {
                    bin.addContextPath(policy.getContextPath());
                    foundBin = true;
                }
            }

            if (!foundBin) {
                policies.add(new ContextPolicyBin(serviceReader).realm(policy.getRealm())
                        .addClaimsMap(policyRequiredAttributes)
                        .authTypes(policy.getAuthenticationMethods())
                        .addContextPath(policy.getContextPath()));
            }
        }

        return new ContextPolicyBin.ContextPolicies(serviceReader).addAll(policies);
    }

    private boolean hasSameRequiredAttributes(ContextPolicyBin bin,
            Map<String, String> mappingsToCheck) {

        if (!(bin.claimsMapping()
                .keySet()
                .containsAll(mappingsToCheck.keySet()) && mappingsToCheck.keySet()
                .containsAll(bin.claimsMapping()
                        .keySet()))) {
            return false;
        }

        return !bin.claimsMapping()
                .entrySet()
                .stream()
                .filter(binMapping -> !mappingsToCheck.get(binMapping.getKey())
                        .equals(binMapping.getValue()))
                .findFirst()
                .isPresent();
    }

    public Map<String, Object> whiteListToPolicyManagerProps(ContextPath.ContextPaths contexts) {
        List<String> serviceContexts =
                contexts.getValue() == null ? new ArrayList<>() : contexts.getValue();
        return ImmutableMap.of(WHITE_LIST_CONTEXT, serviceContexts);
    }

    public static List<String> getWhitelistContexts(ServiceActions serviceActions) {
        Object whitelistProp = serviceActions.read(POLICY_MANAGER_PID)
                .get(WHITE_LIST_CONTEXT);

        if (whitelistProp != null && whitelistProp instanceof String[]) {
            return new ServiceCommons().resolveProperties((String[]) whitelistProp);
        }

        return new ArrayList<>();
    }
}
