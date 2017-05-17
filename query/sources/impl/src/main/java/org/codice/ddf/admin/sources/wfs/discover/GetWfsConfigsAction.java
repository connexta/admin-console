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
package org.codice.ddf.admin.sources.wfs.discover;

import static org.codice.ddf.admin.common.services.ServiceCommons.serviceConfigurationExists;
import static org.codice.ddf.admin.sources.commons.SourceActionCommons.getSourceConfigurations;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.SERVICE_PROPS_TO_WFS_CONFIG;
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.WFS_FACTORY_PIDS;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.internal.admin.configurator.opfactory.AdminOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.ServiceReader;

import com.google.common.collect.ImmutableList;

public class GetWfsConfigsAction extends BaseAction<ListField<SourceInfoField>> {

    public static final String ID = "wfsConfigs";

    public static final String DESCRIPTION =
            "Retrieves all currently configured WFS sources. If a source pid is specified, only that source configuration will be returned.";

    private PidField pid;

    private final AdminOpFactory adminOpFactory;

    private final ManagedServiceOpFactory managedServiceOpFactory;

    private final ServiceReader serviceReader;

    public GetWfsConfigsAction(AdminOpFactory adminOpFactory,
            ManagedServiceOpFactory managedServiceOpFactory, ServiceReader serviceReader) {
        super(ID, DESCRIPTION, new ListFieldImpl<>(SourceInfoField.class));
        this.adminOpFactory = adminOpFactory;
        this.managedServiceOpFactory = managedServiceOpFactory;
        this.serviceReader = serviceReader;
        pid = new PidField();
    }

    @Override
    public ListField<SourceInfoField> performAction() {
        return getSourceConfigurations(WFS_FACTORY_PIDS,
                SERVICE_PROPS_TO_WFS_CONFIG,
                pid.getValue(),
                adminOpFactory,
                managedServiceOpFactory,
                serviceReader);
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (pid.getValue() != null) {
            addMessages(serviceConfigurationExists(pid, adminOpFactory));
        }
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(pid);
    }
}
