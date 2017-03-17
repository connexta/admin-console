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
package org.codice.ddf.admin.query.commons.fields.common.message;

import java.util.List;

import org.codice.ddf.admin.query.api.action.Message;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;


public class MessageField extends BaseObjectField implements Message {

    public static final String FIELD_NAME = "message";
    public static final String DESCRIPTION = "A message containing a code with a summary of the message and content will a more in depth description.";

    private MessageType messageType;
    private MessageCodeField code;
    private StringField content;
    private String messagePath;

    public MessageField(MessageType messageType) {
        super(FIELD_NAME, FIELD_NAME, DESCRIPTION);
        this.messageType = messageType;
        this.code = new MessageCodeField();
        this.content = new StringField("content");
    }

    public MessageField(String code, String content, MessageType messageType) {
        this(messageType);
        this.code.setValue(code);
        this.content.setValue(content);
        this.messageType = messageType;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(code, content);
    }

    @Override
    public String code() {
        return code.getValue();
    }

    @Override
    public String content() {
        return content.getValue();
    }

    @Override
    public MessageType messageType() {
        return messageType;
    }

    @Override
    public List<Message> validate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String messagePath() {
        return messagePath;
    }

    @Override
    public void addMessageSubPath(String subPath) {
        messagePath = subPath + MESSAGE_PATH_DELIMETER + messagePath;
    }
}
