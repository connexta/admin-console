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
package org.codice.ddf.admin.beta.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.Operation;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.configurator.Result;

public class MockConfigurator implements Configurator {

    Map<UUID, Operation> operations = new HashMap<>();

    @Override
    public OperationReport commit(String s, String... strings) {

        Map<UUID, Result> results = new HashMap<>();

        operations.forEach((uuid, operation) -> results.put(uuid, operation.commit()));

        return new OperationReport() {
            @Override
            public boolean hasTransactionSucceeded() {
                return getFailedResults().size() == 0;
            }

            @Override
            public Result getResult(UUID uuid) {
                return results.get(uuid);
            }

            @Override
            public List<Result> getFailedResults() {
                List<Result> failures = new ArrayList<>();

                results.forEach((uuid, result) -> {
                    if (!result.isOperationSucceeded()) {
                        failures.add(result);
                    }
                });

                return failures;
            }

            @Override
            public boolean containsFailedResults() {
                return getFailedResults().size() > 0;
            }

            @Override
            public void putResult(UUID uuid, Result result) {
                results.put(uuid, result);
            }
        };
    }

    @Override
    public UUID add(Operation operation) {
        UUID uuid = UUID.randomUUID();
        operations.put(uuid, operation);
        return uuid;
    }
}
