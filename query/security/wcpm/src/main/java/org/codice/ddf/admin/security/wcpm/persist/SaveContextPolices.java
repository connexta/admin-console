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
package org.codice.ddf.admin.security.wcpm.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.security.common.SecurityMessages.noRootContextError;
import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.POLICY_MANAGER_PID;
import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.ROOT_CONTEXT_PATH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.security.common.SecurityValidation;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;
import org.codice.ddf.admin.security.common.fields.wcpm.ContextPolicyBin;
import org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties;
import org.codice.ddf.admin.security.common.services.StsServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableList;

public class SaveContextPolices extends BaseFunctionField<ListField<ContextPolicyBin>> {

    public static final String FUNCTION_FIELD_NAME = "saveContextPolicies";

    public static final String DESCRIPTION =
            "Saves a list of policies to be applied to their corresponding context paths.";

    private ConfiguratorFactory configuratorFactory;

    private final ServiceActions serviceActions;

    private ListField<ContextPolicyBin> contextPolicies;

    private StsServiceProperties stsServiceProps;

    public SaveContextPolices(ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions) {
        super(FUNCTION_FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(ContextPolicyBin.class));
        this.configuratorFactory = configuratorFactory;
        this.serviceActions = serviceActions;

        contextPolicies = new ListFieldImpl<>("policies",
                new ContextPolicyBin().useDefaultRequiredFields()).isRequired(true);
        updateArgumentPaths();

        stsServiceProps = new StsServiceProperties();
    }

    @Override
    public ListField<ContextPolicyBin> performFunction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.add(serviceActions.build(POLICY_MANAGER_PID,
                new PolicyManagerServiceProperties().contextPoliciesToPolicyManagerProps(
                        contextPolicies.getList()),
                true));

        OperationReport configReport = configurator.commit(
                "Web Context Policy saved with details: {}",
                contextPolicies.toString());

        if (configReport.containsFailedResults()) {
            addResultMessage(failedPersistError());
        }

        return containsErrorMsgs() ? null : contextPolicies;
    }

    @Override
    public void validate() {
        super.validate();
        checkRootPathExists();

        if(containsErrorMsgs()) {
            return;
        }

        List<StringField> claimArgs = new ArrayList<>();
        for (ContextPolicyBin bin : contextPolicies.getList()) {
            claimArgs.addAll(bin.claimsMappingField()
                    .getList()
                    .stream()
                    .map(ClaimsMapEntry::claimField)
                    .collect(Collectors.toList()));
        }

        addMessages(SecurityValidation.validateStsClaimsExist(claimArgs, serviceActions, stsServiceProps));
    }

    private void checkRootPathExists() {
        if (contextPolicies.getList()
                .stream()
                .map(ContextPolicyBin::contexts)
                .flatMap(Collection::stream)
                .noneMatch(ROOT_CONTEXT_PATH::equals)) {
            addArgumentMessage(noRootContextError(contextPolicies.path()));
        }
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(contextPolicies);
    }

    @Override
    public FunctionField<ListField<ContextPolicyBin>> newInstance() {
        return new SaveContextPolices(configuratorFactory, serviceActions);
    }
}
