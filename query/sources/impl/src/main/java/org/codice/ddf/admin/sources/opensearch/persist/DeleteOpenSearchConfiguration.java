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

import static org.codice.ddf.admin.common.message.DefaultMessages.failedDeleteError;
import static org.codice.ddf.admin.common.message.DefaultMessages.noExistingConfigError;
import static org.codice.ddf.admin.common.services.ServiceCommons.configExists;
import static org.codice.ddf.admin.common.services.ServiceCommons.delete;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.ServicePid;

import com.google.common.collect.ImmutableList;

public class DeleteOpenSearchConfiguration extends BaseAction<BooleanField> {
    public static final String ID = "deleteOpenSearchSource";

    public static final String DESCRIPTION =
            "Deletes an OpenSearch source configuration and returns the deleted configuration.";

    private ServicePid servicePid;

    private ConfiguratorFactory configuratorFactory;

    public DeleteOpenSearchConfiguration(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new BooleanField());
        this.configuratorFactory = configuratorFactory;
        servicePid = new ServicePid();
        servicePid.isRequired(true);
    }

    @Override
    public BooleanField performAction() {
        if(!delete(servicePid.getValue(), configuratorFactory)) {
            addArgumentMessage(failedDeleteError(servicePid.path()));
            return null;
        }
        return new BooleanField(true);
    }

    @Override
    public void validate() {
        super.validate();
        if(containsErrorMsgs()) {
            return;
        }

        if(!configExists(servicePid.getValue(), configuratorFactory)) {
            addArgumentMessage(noExistingConfigError(servicePid.path()));
        }
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(servicePid);
    }
}
