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
package org.codice.ddf.admin.common.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.api.report.Report;

import com.google.common.collect.ImmutableList;

public class ReportImpl implements Report {

    private List<ErrorMessage> argumentMessages;

    private List<ErrorMessage> resultMessages;

    public ReportImpl() {
        argumentMessages = new ArrayList<>();
        resultMessages = new ArrayList<>();
    }

    @Override
    public List<ErrorMessage> argumentMessages() {
        return ImmutableList.copyOf(argumentMessages);
    }

    @Override
    public List<ErrorMessage> resultMessages() {
        return ImmutableList.copyOf(resultMessages);
    }

    @Override
    public List<ErrorMessage> messages() {
        return ListUtils.union(argumentMessages, resultMessages);
    }

    public ReportImpl addArgumentMessages(List<ErrorMessage> messages) {
        argumentMessages.addAll(messages);
        return this;
    }

    public ReportImpl addArgumentMessage(ErrorMessage message) {
        argumentMessages.add(message);
        return this;
    }

    public ReportImpl addResultMessages(List<ErrorMessage> messages) {
        resultMessages.addAll(messages);
        return this;
    }

    public ReportImpl addResultMessage(ErrorMessage message) {
        resultMessages.add(message);
        return this;
    }

    public ReportImpl addMessages(Report report) {
        return addResultMessages(report.resultMessages()).addArgumentMessages(report.argumentMessages());
    }

    public boolean containsErrorMsgs() {
        return !messages().isEmpty();
    }
}
