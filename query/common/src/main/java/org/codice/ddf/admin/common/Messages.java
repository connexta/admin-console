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

public class Messages {

    private List<Message> argumentMessages;

    private List<Message> resultMessages;

    public Messages() {
        argumentMessages = new ArrayList<>();
        resultMessages = new ArrayList<>();
    }

    public List<Message> argumentMessages() {
        return argumentMessages;
    }

    public Messages argumentMessages(List<Message> messages) {
        argumentMessages = messages;
        return this;
    }

    public Messages argumentMessage(Message message) {
        argumentMessages.add(message);
        return this;
    }

    public List<Message> resultMessages() {
        return resultMessages;
    }

    public Messages resultMessages(List<Message> messages) {
        resultMessages = messages;
        return this;
    }

    public Messages resultMessage(Message message) {
        resultMessages.add(message);
        return this;
    }

    public List<Message> allMessages() {
        return ListUtils.union(argumentMessages, resultMessages);
    }

    public Messages copyResultMessages(Messages messages) {
        return resultMessages(messages.resultMessages());
    }

    public Messages copyArgumentMessages(Messages messages) {
        return argumentMessages(messages.resultMessages());
    }

    public boolean hasErrors() {
        return !argumentMessages.stream()
                .filter(message -> message.getType() == Message.MessageType.ERROR)
                .collect(Collectors.toList())
                .isEmpty();
    }
}
