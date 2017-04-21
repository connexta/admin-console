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
package org.codice.ddf.admin.security.wcpm.actions.persist;

import static org.codice.ddf.admin.common.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.message.DefaultMessages.invalidClaimType;
import static org.codice.ddf.admin.common.message.DefaultMessages.invalidFieldError;
import static org.codice.ddf.admin.common.message.DefaultMessages.noRootContextError;
import static org.codice.ddf.admin.security.common.fields.wcpm.services.PolicyManagerServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID;
import static org.codice.ddf.admin.security.common.fields.wcpm.services.PolicyManagerServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS;
import static org.codice.ddf.admin.security.wcpm.commons.ContextPolicyServiceProperties.POLICY_MANAGER_PID;
import static org.codice.ddf.admin.security.wcpm.commons.ContextPolicyServiceProperties.ROOT_CONTEXT_PATH;
import static org.codice.ddf.admin.security.wcpm.commons.ContextPolicyServiceProperties.contextPoliciesToPolicyManagerProps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin;
import org.codice.ddf.admin.security.common.fields.wcpm.services.PolicyManagerServiceProperties;

import com.google.common.collect.ImmutableList;

public class SaveContextPolices extends BaseAction<ListField<ContextPolicyBin>> {

    public static final String DEFAULT_FIELD_NAME = "saveContextPolicies";

    public static final String DESCRIPTION =
            "Saves a list of policies to be applied to their corresponding context paths.";

    private ConfiguratorFactory configuratorFactory;

    private ListField<ContextPolicyBin> contextPolicies;

    private PolicyManagerServiceProperties wcpmServiceProps = new PolicyManagerServiceProperties();

    public SaveContextPolices(ConfiguratorFactory configuratorFactory) {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(ContextPolicyBin.class));
        contextPolicies = new ListFieldImpl<>("policies", ContextPolicyBin.class);
        contextPolicies.isRequired(true);
        contextPolicies.getListFieldType().claimsMappingField().getListFieldType().isRequired(true);
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(contextPolicies);
    }

    @Override
    public ListField<ContextPolicyBin> performAction() {
        checkRootPathExists();
        checkClaimsValidity();

        // Persist if no errors
        if (!containsErrorMsgs()) {
            Configurator configurator = configuratorFactory.getConfigurator();
            configurator.updateConfigFile(POLICY_MANAGER_PID,
                    contextPoliciesToPolicyManagerProps(contextPolicies),
                    true);

            // TODO: 4/14/17 Fix the contextPolicies.toString(), this will print the objects name
            OperationReport configReport = configurator.commit(
                    "Web Context Policy saved with details: {}",
                    contextPolicies.toString());

            if (configReport.containsFailedResults()) {
                addArgumentMessage(failedPersistError(name()));
            }
        }

        return wcpmServiceProps.contextPolicyServiceToContextPolicyFields(configuratorFactory);
    }

    private void checkClaimsValidity() {
        Map<String, Object> stsConfig = configuratorFactory.getConfigReader()
                .getConfig(STS_CLAIMS_CONFIGURATION_CONFIG_ID);

        List<String> validClaims = (stsConfig != null) ?
                Arrays.asList((String[]) stsConfig.get(STS_CLAIMS_PROPS_KEY_CLAIMS)) :
                new ArrayList<>();

        List<Map<String, String>> claims = contextPolicies.getList()
                .stream()
                .map(ContextPolicyBin::claimsMapping)
                .collect(Collectors.toList());

        if (claims.stream()
                .map(policy -> policy.keySet())
                .flatMap(Collection::stream)
                .anyMatch(claim -> !validClaims.contains(claim))) {
            addArgumentMessage(invalidClaimType(name()));
        }

//        if(claims.stream()
//                .map(Map::values)
//                .flatMap(Collection::stream)
//                .anyMatch(value -> value == null || value.isEmpty())) {
//            addArgumentMessage(invalidFieldError(name()));
//        }
    }

    private void checkRootPathExists() {
        if (contextPolicies.getList()
                .stream()
                .map(ContextPolicyBin::contexts)
                .flatMap(Collection::stream)
                .noneMatch(ROOT_CONTEXT_PATH::equals)) {
            addArgumentMessage(noRootContextError(name()));
        }
    }
}
