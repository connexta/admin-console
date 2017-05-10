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
package org.codice.ddf.admin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.ListUtils;
import org.codice.ddf.admin.api.action.Message;

public class Report {

    private List<Message> argumentMessages;

    private List<Message> resultMessages;

    public Report() {
        argumentMessages = new ArrayList<>();
        resultMessages = new ArrayList<>();
    }

    public List<Message> argumentMessages() {
        return argumentMessages;
    }

    public Report argumentMessages(List<Message> messages) {
        argumentMessages.addAll(messages);
        return this;
    }

    public Report argumentMessage(Message message) {
        argumentMessages.add(message);
        return this;
    }

    public List<Message> resultMessages() {
        return resultMessages;
    }

    public Report resultMessages(List<Message> messages) {
        resultMessages.addAll(messages);
        return this;
    }

    public Report resultMessage(Message message) {
        resultMessages.add(message);
        return this;
    }

    public List<Message> messages() {
        return ListUtils.union(argumentMessages, resultMessages);
    }

    public Report addMessages(Report report) {
        resultMessages(report.resultMessages());
        return argumentMessages(report.argumentMessages());
    }

    public boolean containsErrorMsgs() {
        return !messages().stream()
                .filter(message -> message.getType() == Message.MessageType.ERROR)
                .collect(Collectors.toList())
                .isEmpty();
    }
}
