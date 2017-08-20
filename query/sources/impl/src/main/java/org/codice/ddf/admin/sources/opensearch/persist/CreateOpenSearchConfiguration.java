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
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FEATURE;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.openSearchConfigToServiceProps;

import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.sources.SourceMessages;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.SourceValidationUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class CreateOpenSearchConfiguration extends BaseFunctionField<BooleanField> {

    public static final String FIELD_NAME = "createOpenSearchSource";

    public static final String DESCRIPTION = "Creates an OpenSearch source configuration.";

    public static final BooleanField RETURN_TYPE = new BooleanField();

    private OpenSearchSourceConfigurationField config;

    private SourceValidationUtils sourceValidationUtils;

    private ServiceCommons serviceCommons;

    private final ConfiguratorSuite configuratorSuite;

    public CreateOpenSearchConfiguration(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        config = new OpenSearchSourceConfigurationField();
        config.useDefaultRequired();
        updateArgumentPaths();

        sourceValidationUtils = new SourceValidationUtils(configuratorSuite);
        serviceCommons = new ServiceCommons(configuratorSuite);
    }

    @Override
    public BooleanField performFunction() {
        Configurator configurator = configuratorSuite.getConfiguratorFactory()
                .getConfigurator();
        configurator.add(configuratorSuite.getFeatureActions()
                .start(OPENSEARCH_FEATURE));
        OperationReport report = configurator.commit("Starting feature [{}]", OPENSEARCH_FEATURE);

        if (report.containsFailedResults()) {
            addErrorMessage(failedPersistError());
            return new BooleanField(false);
        }
        addErrorMessages(serviceCommons.createManagedService(openSearchConfigToServiceProps(config),
                OPENSEARCH_FACTORY_PID));
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }
        addErrorMessages(sourceValidationUtils.duplicateSourceNameExists(config.sourceNameField()));
    }

    @Override
    public BooleanField getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new CreateOpenSearchConfiguration(configuratorSuite);
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(DefaultMessages.FAILED_PERSIST,
                SourceMessages.DUPLICATE_SOURCE_NAME);
    }
}
