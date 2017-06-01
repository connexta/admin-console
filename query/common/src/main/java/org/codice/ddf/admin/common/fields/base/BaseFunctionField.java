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
package org.codice.ddf.admin.common.fields.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.report.FunctionReportImpl;

import com.google.common.collect.ImmutableList;

public abstract class BaseFunctionField<T extends DataType> extends BaseField<Map<String, Object>, FunctionReport<T>> implements FunctionField<T> {

    private FunctionReportImpl<T> report;

    private T returnType;

    public BaseFunctionField(String functionName, String description) {
        super(functionName, description);
    }

    public BaseFunctionField(String functionName, String description, T returnType) {
        super(functionName, description);
        this.returnType = returnType;
        report = new FunctionReportImpl<>();
    }

    public BaseFunctionField(String functionName, String description, Class<T> returnType) {
        super(functionName, description);
        try {
            this.returnType = returnType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(String.format("Unable to create new instance of class [%s]. Ensure there is a default constructor for the BaseFunctionField to initialize.", returnType.getClass()));
        }
        report = new FunctionReportImpl<>();
    }

    public abstract T performFunction();

    @Override
    public T getReturnType() {
        return returnType;
    }

    @Override
    public void setValue(Map<String, Object> args) {
        if (args == null || args.isEmpty()) {
            return;
        }

        getArguments().stream()
                .filter(field -> args.containsKey(field.fieldName()))
                .forEach(field -> field.setValue(args.get(field.fieldName())));
    }

    @Override
    public FunctionReport<T> getValue() {
        validate();
        if (!report.containsErrorMsgs()) {
            report.result(performFunction());
        }

        return report;
    }

    @Override
    public void updatePath(List<String> subPath) {
        super.updatePath(subPath);
        updateArgumentPaths();
    }

    @Override
    public void fieldName(String fieldName) {
        super.fieldName(fieldName);
        updateArgumentPaths();
    }

    public void setReturnType(T returnType) {
        this.returnType = returnType;
    }

    public void updateArgumentPaths() {
        List<String> argPath = new ImmutableList.Builder<String>().addAll(path())
                .add(ARGUMENT)
                .build();
        getArguments().forEach(arg -> arg.updatePath(argPath));
    }

    public void validate() {
        getArguments().stream()
                .map(DataType::validate)
                .flatMap(Collection<Message>::stream)
                .forEach(msg -> addArgumentMessage(msg));
    }

    protected boolean containsErrorMsgs() {
        return report.containsErrorMsgs();
    }

    protected BaseFunctionField addArgumentMessages(List<Message> msgs) {
        msgs.forEach(msg -> addArgumentMessage(msg));
        return this;
    }

    protected BaseFunctionField addArgumentMessage(Message msg) {
        Message copy = msg.copy();
        if(copy.getPath().isEmpty()) {
            copy.setPath(path());
        }

        report.addArgumentMessage(copy);
        return this;
    }

    protected BaseFunctionField addResultMessages(List<Message> msgs) {
        msgs.forEach(this::addResultMessage);
        return this;
    }

    protected BaseFunctionField addResultMessage(Message msg) {
        Message copy = msg.copy();
        List<String> copyMsgPath = copy.getPath();

        //Remove first element of path because the return object's name will be included in the path
        if(!copyMsgPath.isEmpty()) {
            copyMsgPath.remove(0);
        }

        List<String> fullPath = new ImmutableList.Builder<String>().addAll(path())
                .addAll(copyMsgPath)
                .build();
        copy.setPath(fullPath);
        report.addResultMessage(copy);
        return this;
    }

    protected BaseFunctionField addMessages(Report report) {
        addArgumentMessages(report.argumentMessages());
        addResultMessages(report.resultMessages());
        return this;
    }
}
