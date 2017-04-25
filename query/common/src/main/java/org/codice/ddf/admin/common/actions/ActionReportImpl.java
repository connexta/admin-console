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
package org.codice.ddf.admin.common.actions;

import static org.codice.ddf.admin.api.fields.Field.INDEX_DELIMETER;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.action.ActionReport;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;

public class ActionReportImpl<T extends Field> implements ActionReport<T> {

    private List<Message> messages;

    private T result;

    public ActionReportImpl() {
        this.messages = new ArrayList<>();
    }

    @Override
    public List<Message> messages() {
        return messages;
    }

    @Override
    public T result() {
        return result;
    }

    @Override
    public void result(T result) {
        this.result = result;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean containsErrorMsgs() {
        return messages().stream()
                .anyMatch(msg -> msg.getType() == Message.MessageType.ERROR);
    }
}
