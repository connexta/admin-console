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

import static org.codice.ddf.admin.common.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.services.ServiceCommons.createManagedService;
import static org.codice.ddf.admin.common.services.ServiceCommons.serviceConfigurationExists;
import static org.codice.ddf.admin.common.services.ServiceCommons.updateService;
import static org.codice.ddf.admin.sources.commons.utils.SourceValidationUtils.hasSourceName;
import static org.codice.ddf.admin.sources.commons.utils.SourceValidationUtils.validateSourceName;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.openSearchConfigToServiceProps;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.internal.admin.configurator.opfactory.AdminOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.ServiceReader;

import com.google.common.collect.ImmutableList;

public class SaveOpenSearchConfiguration extends BaseAction<BooleanField> {

    public static final String ID = "saveOpenSearchSource";

    public static final String DESCRIPTION =
            "Saves an OpenSearch source configuration. If a pid is specified, the source configuration specified by the pid will be updated. Returns true on success and false on failure.";

    private OpenSearchSourceConfigurationField config;

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    private final AdminOpFactory adminOpFactory;

    private final ManagedServiceOpFactory managedServiceOpFactory;

    private final ServiceReader serviceReader;

    public SaveOpenSearchConfiguration(ConfiguratorFactory configuratorFactory,
            AdminOpFactory adminOpFactory, ManagedServiceOpFactory managedServiceOpFactory,
            ServiceReader serviceReader) {
        super(ID, DESCRIPTION, new BooleanField());
        this.serviceReader = serviceReader;
        config = new OpenSearchSourceConfigurationField();
        pid = new PidField();
        config.isRequired(true);
        config.sourceNameField()
                .isRequired(true);
        config.endpointUrlField()
                .isRequired(true);

        this.configuratorFactory = configuratorFactory;
        this.adminOpFactory = adminOpFactory;
        this.managedServiceOpFactory = managedServiceOpFactory;
    }

    @Override
    public BooleanField performAction() {
        if (StringUtils.isNotEmpty(pid.getValue())) {
            addMessages(updateService(pid,
                    openSearchConfigToServiceProps(config),
                    configuratorFactory,
                    adminOpFactory));
        } else {
            if (createManagedService(openSearchConfigToServiceProps(config),
                    OPENSEARCH_FACTORY_PID,
                    configuratorFactory,
                    managedServiceOpFactory).containsErrorMsgs()) {
                addArgumentMessage(failedPersistError(config.path()));
            }
        }
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (pid.getValue() != null) {
            addMessages(serviceConfigurationExists(pid, adminOpFactory));
            if (!containsErrorMsgs() && !hasSourceName(pid.getValue(),
                    config.sourceName(),
                    serviceReader)) {
                addMessages(validateSourceName(config.sourceNameField(), serviceReader));
            }
        } else {
            addMessages(validateSourceName(config.sourceNameField(), serviceReader));
        }
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config, pid);
    }
}
