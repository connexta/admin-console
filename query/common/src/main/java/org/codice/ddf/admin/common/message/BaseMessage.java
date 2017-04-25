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
package org.codice.ddf.admin.common.message;

import java.util.LinkedList;
import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;

public abstract class BaseMessage implements Message {

    private MessageType type;

    private String code;

    private List<String> path;

    public BaseMessage(MessageType type, String code) {
        this.type = type;
        this.code = code;
        path = new LinkedList<>();
    }

    public BaseMessage(MessageType type, String code, String pathOrigin) {
        this(type, code);
        path.add(pathOrigin);
    }

    public BaseMessage(MessageType type, String code, List<String> path) {
        this(type, code);
        this.path.addAll(path);
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public List<String> getPath() {
        return path;
    }

    @Override
    public BaseMessage addSubpath(String subPath) {
        path.add(0, subPath);
        return this;
    }

    @Override
    public Message setPath(List<String> path) {
        this.path = path;
        return this;
    }
}