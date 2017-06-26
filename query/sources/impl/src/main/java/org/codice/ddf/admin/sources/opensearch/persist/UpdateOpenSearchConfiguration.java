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
package org.codice.ddf.admin.sources.opensearch.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FEATURE;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.openSearchConfigToServiceProps;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
import org.codice.ddf.admin.sources.utils.SourceValidationUtils;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

import com.google.common.collect.ImmutableList;

public class UpdateOpenSearchConfiguration extends BaseFunctionField<BooleanField> {

    public static final String FIELD_NAME = "updateOpenSearchSource";

    public static final String DESCRIPTION =
            "Updates an OpenSearch source configuration specified by the pid. Returns true on success and false on failure.";

    private OpenSearchSourceConfigurationField config;

    private SourceValidationUtils sourceValidationUtils;

    private SourceUtilCommons sourceUtilCommons;

    private final ConfiguratorFactory configuratorFactory;

    private final ServiceActions serviceActions;

    private final ManagedServiceActions managedServiceActions;

    private final ServiceReader serviceReader;

    private final FeatureActions featureActions;

    public UpdateOpenSearchConfiguration(ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions, ManagedServiceActions managedServiceActions,
            ServiceReader serviceReader, FeatureActions featureActions) {

        super(FIELD_NAME, DESCRIPTION, new BooleanField());
        this.configuratorFactory = configuratorFactory;
        this.serviceActions = serviceActions;
        this.managedServiceActions = managedServiceActions;
        this.serviceReader = serviceReader;
        this.featureActions = featureActions;

        config = new OpenSearchSourceConfigurationField();
        config.useDefaultRequired();
        config.pidField().isRequired(true);
        updateArgumentPaths();

        sourceValidationUtils = new SourceValidationUtils(serviceReader,
                managedServiceActions,
                configuratorFactory,
                serviceActions);
        sourceUtilCommons = new SourceUtilCommons(managedServiceActions,
                serviceActions,
                serviceReader,
                configuratorFactory);
    }

    @Override
    public BooleanField performFunction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.add(featureActions.start(OPENSEARCH_FEATURE));
        OperationReport report = configurator.commit("Starting feature [{}]", OPENSEARCH_FEATURE);

        if(report.containsFailedResults()) {
            addResultMessage(failedPersistError());
        }

        addMessages(sourceUtilCommons.updateSource(config.pidField(),
                openSearchConfigToServiceProps(config)));
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }
        addMessages(sourceValidationUtils.validateSourceName(config.sourceNameField(), config.pidField()));
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new UpdateOpenSearchConfiguration(configuratorFactory,
                serviceActions,
                managedServiceActions,
                serviceReader, featureActions);
    }
}
