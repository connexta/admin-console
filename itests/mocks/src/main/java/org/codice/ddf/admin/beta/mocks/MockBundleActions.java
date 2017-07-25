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

import static org.codice.ddf.admin.configurator.Status.COMMIT_PASSED;
import static org.codice.ddf.admin.configurator.Status.ROLLBACK_PASSED;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.admin.configurator.Operation;
import org.codice.ddf.admin.configurator.Result;
import org.codice.ddf.admin.configurator.Status;
import org.codice.ddf.internal.admin.configurator.actions.BundleActions;

public class MockBundleActions implements BundleActions {

    private Map<String, Boolean> bundles = new HashMap<>();

    private Result makeResult(Status status) {
        return new Result<Void>() {
            @Override
            public boolean isOperationSucceeded() {
                return true;
            }

            @Override
            public Status getStatus() {
                return status;
            }

            @Override
            public Optional<ConfiguratorException> getError() {
                return Optional.empty();
            }

            @Override
            public Optional<Void> getOperationData() {
                return Optional.of(null);
            }
        };
    }

    private Operation makeOperation(Supplier<Result<Void>> commit, Supplier<Result<Void>> rollback) {
        return new Operation<Void>() {
            @Override
            public Result<Void> commit() throws ConfiguratorException {
                return commit.get();
            }

            @Override
            public Result<Void> rollback() throws ConfiguratorException {
                return rollback.get();
            }
        };
    }

    @Override
    public Operation<Void> start(String bundleSymName) throws ConfiguratorException {
        return makeOperation(() -> {
            bundles.put(bundleSymName, true);
            return makeResult(COMMIT_PASSED);
        }, () -> makeResult(ROLLBACK_PASSED));
    }

    @Override
    public Operation<Void> stop(String bundleSymName) throws ConfiguratorException {
        return makeOperation(() -> {
            bundles.remove(bundleSymName);
            return makeResult(COMMIT_PASSED);
        }, () -> makeResult(ROLLBACK_PASSED));
    }

    @Override
    public boolean isStarted(String bundleSymName) throws ConfiguratorException {
        return bundles.containsKey(bundleSymName);
    }
}

