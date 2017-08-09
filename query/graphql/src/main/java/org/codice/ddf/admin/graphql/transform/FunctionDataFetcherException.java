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
import java.util.Map;

import org.boon.Boon;
import org.codice.ddf.admin.api.report.ErrorMessage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class FunctionDataFetcherException extends RuntimeException {

    private List<ErrorMessage> customMessages;

    private static final List<String> BLACK_LIST_WORDS = ImmutableList.of("password");

    private static final String HIDDEN_FLAG = "*****";

    public FunctionDataFetcherException(String functionName, List<Object> args,
            List<ErrorMessage> customMessages) {
        super(filterString(functionName, args, customMessages));
        this.customMessages = customMessages;
    }

    public List<ErrorMessage> getCustomMessages() {
        return customMessages;
    }

    /**
     * Overrides the {@code fillInStackTrace} method to suppress the stack trace that is
     * printed by GraphQL.
     **/
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public static String filterString(String functionName, List<Object> args,
            List<ErrorMessage> customMessage) {
        return Boon.toPrettyJson(blackList(toMap(functionName, args, customMessage)));
    }

    private static Object blackList(Object result) {
        if (result instanceof List) {
            ((List) result).forEach(FunctionDataFetcherException::blackList);
        } else if (result instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) result).entrySet()) {
                if (entry.getValue() instanceof Map || entry.getValue() instanceof List) {
                    blackList(entry.getValue());
                } else {
                    boolean containsBlackListWord = BLACK_LIST_WORDS.stream()
                            .anyMatch(blackListWord -> entry.getKey()
                                    .toLowerCase()
                                    .contains(blackListWord.toLowerCase()));
                    if (containsBlackListWord) {
                        entry.setValue(HIDDEN_FLAG);
                    }
                }
            }
        }
        return result;
    }

    private static Map<String, Object> toMap(String functionName, List<Object> args,
            List<ErrorMessage> customMessage) {
        return ImmutableMap.of("functionName", functionName, "args", args, "errors", customMessage);
    }
}
