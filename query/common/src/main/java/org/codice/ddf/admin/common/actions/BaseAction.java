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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.api.action.ActionReport;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAction<T extends Field> implements Action<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    private String name;

    private String description;

    private T returnType;

    private ActionReportImpl<T> report;

    public BaseAction(String name, String description, T returnType) {
        this.name = name;
        this.description = description;
        this.returnType = returnType;
        report = new ActionReportImpl<T>();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public T returnType() {
        return returnType;
    }

    @Override
    public void setArguments(Map<String, Object> args) {
        if (args == null || args.isEmpty()) {
            return;
        }

        getArguments().stream()
                .filter(field -> args.containsKey(field.fieldName()))
                .forEach(field -> field.setValue(args.get(field.fieldName())));

        // TODO: tbatie - 3/16/17 - Add logger if a fieldName is not found
    }

    @Override
    public ActionReport<T> process() {
        validate();
        if (!report.containsErrorMsgs()) {
            report.result(performAction());
        }

        return report;
    }

    protected boolean containsErrorMsgs() {
        return report.containsErrorMsgs();
    }

    protected BaseAction addArgumentMessage(Message msg) {
        Message copy = msg.copy();
        copy.addSubpath(name);
        copy.addSubpath(ARGUMENT);
        report.addMessage(copy);
        return this;
    }

    protected BaseAction addMessage(Message msg) {
        Message copy = msg.copy();
        copy.addSubpath(name);
        report.addMessage(copy);
        return this;
    }

    protected BaseAction addArgumentMessages(List<Message> msgs) {
        msgs.forEach(msg -> addArgumentMessage(msg));
        return this;
    }

    protected BaseAction addMessages(List<Message> msgs) {
        msgs.forEach(msg -> addMessage(msg));
        return this;
    }

    public void validate() {
        getArguments().stream()
                .map(Field::validate)
                .flatMap(Collection<Message>::stream)
                .forEach(msg -> addArgumentMessage(msg));
    }

    public abstract T performAction();

}
