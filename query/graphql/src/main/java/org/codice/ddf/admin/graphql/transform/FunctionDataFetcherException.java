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
package org.codice.ddf.admin.graphql.transform;

import java.util.List;

import org.boon.Boon;
import org.codice.ddf.admin.api.report.ErrorMessage;

import com.google.common.collect.ImmutableMap;

public class FunctionDataFetcherException extends RuntimeException {

    private List<ErrorMessage> customMessage;

    public FunctionDataFetcherException(String functionName, List<Object> args,
            List<ErrorMessage> customMessage) {
        super(Boon.toPrettyJson(ImmutableMap.of("functionName",
                functionName,
                "args",
                args,
                "errors",
                customMessage)));
        this.customMessage = customMessage;
    }

    public List<ErrorMessage> getCustomMessages() {
        return customMessage;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
