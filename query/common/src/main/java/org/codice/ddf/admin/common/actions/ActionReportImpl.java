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

    public void addMessages(List<Message> messages) {
        this.messages.addAll(messages);
    }

    public void addMessage(Message message) {
        // TODO: tbatie - 4/22/17 - This is a temporary fix for the additional fieldName added to the message path when specifying a list index
        List<String> newPath = new ArrayList<>();
        List<String> msgPath = message.getPath();
        for(int i = 0; i < msgPath.size(); i++) {
            if(i == 0) {
                newPath.add(msgPath.get(i));
            } else if(i > 0 && !msgPath.get(i - 1).contains(INDEX_DELIMETER)) {
                newPath.add(msgPath.get(i));
            }
        }
        message.setPath(newPath);
        messages.add(message);
    }

    public boolean containsErrorMsgs() {
        return messages().stream()
                .anyMatch(msg -> msg.getType() == Message.MessageType.ERROR);
    }
}
