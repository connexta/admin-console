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
package org.codice.ddf.admin.sources.csw.persist;

import static org.codice.ddf.admin.common.services.ServiceCommons.deleteService;
import static org.codice.ddf.admin.common.services.ServiceCommons.serviceConfigurationExists;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;

import com.google.common.collect.ImmutableList;

public class DeleteCswConfiguration extends BaseFunctionField<BooleanField> {

    public static final String ID = "deleteCswSource";

    public static final String DESCRIPTION =
            "Deletes a CSW source configuration provided by the servicePid and returns true on success and false on failure.";

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    public DeleteCswConfiguration(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new BooleanField());
        this.configuratorFactory = configuratorFactory;
        pid = new PidField();
        pid.isRequired(true);
        updateArgumentPaths();
    }

    @Override
    public BooleanField performFunction() {
        addMessages(deleteService(pid, configuratorFactory));
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }
        addMessages(serviceConfigurationExists(pid, configuratorFactory));
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new DeleteCswConfiguration(configuratorFactory);
    }
}
