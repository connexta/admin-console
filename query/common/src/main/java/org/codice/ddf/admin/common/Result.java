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
import java.util.Optional;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.action.Message;

/**
 * A wrapper for an object which also contains argument and result messages.
 *
 * @param <T> type that is wrapped
 */
public class Result<T> {

    private Optional<T> value;

    private List<Message> argumentMessages;

    private List<Message> resultMessages;

    public Result() {
        this.value = Optional.empty();
        argumentMessages = new ArrayList<>();
        resultMessages = new ArrayList<>();
    }

    public Result(T value) {
        this();
        this.value = Optional.of(value);
    }

    public Result<T> value(T value) {
        this.value = Optional.of(value);
        return this;
    }

    public boolean isPresent() {
        return value.isPresent();
    }

    public boolean isNotPresent() {
        return !isPresent();
    }

    public T get() {
        return value.get();
    }

    public List<Message> argumentMessages() {
        return argumentMessages;
    }

    public Result<T> argumentMessages(List<Message> messages) {
        argumentMessages = messages;
        return this;
    }

    public Result<T> argumentMessage(Message message) {
        argumentMessages.add(message);
        return this;
    }

    public List<Message> resultMessages() {
        return resultMessages;
    }

    public Result<T> resultMessages(List<Message> messages) {
        resultMessages = messages;
        return this;
    }

    public Result<T> resultMessage(Message message) {
        resultMessages.add(message);
        return this;
    }

    public boolean hasErrors() {
        return !argumentMessages.stream()
                .filter(message -> message.getType() == Message.MessageType.ERROR)
                .collect(Collectors.toList())
                .isEmpty();
    }
}
