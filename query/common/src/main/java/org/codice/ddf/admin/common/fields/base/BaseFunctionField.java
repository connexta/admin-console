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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.report.FunctionReportImpl;
import org.codice.ddf.admin.common.report.message.DefaultMessages;

import com.google.common.collect.ImmutableList;

public abstract class BaseFunctionField<T extends DataType> extends BaseField<Map<String, Object>, FunctionReport<T>> implements FunctionField<T> {

    private FunctionReportImpl<T> report;

    private String description;

    public BaseFunctionField(String functionName, String description) {
        super(functionName, description);
        this.description = description;
        report = new FunctionReportImpl<>();
    }

    @Override
    public String description() {
        if (this.getErrorCodes() != null) {
            return String.format("%s The possible errors are:    \r\n %s.",
                    description,
                    formatErrorCodes(getErrorCodes()));
        }
        return super.description();
    }

    public abstract T performFunction();

    public Set<String> getFunctionErrorCodes() {
        Set<String> errors = new HashSet<>();
        errors.add(DefaultMessages.MISSING_REQUIRED_FIELD);
        return errors;
    }

    public Set<String> getErrorCodes() {
        Set<String> allErrorCodes = new HashSet<>();
        for (DataType arg : getArguments()) {
            Stream.of(allErrorCodes, arg.getErrorCodes())
                    .forEach(allErrorCodes::addAll);
        }

        allErrorCodes.addAll(getFunctionErrorCodes());
        return allErrorCodes;
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
    public void pathName(String pathName) {
        super.pathName(pathName);
        updateArgumentPaths();
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
                .flatMap(Collection<ErrorMessage>::stream)
                .forEach(this::addArgumentMessage);
    }

    protected boolean containsErrorMsgs() {
        return report.containsErrorMsgs();
    }

    protected BaseFunctionField addArgumentMessages(List<ErrorMessage> msgs) {
        msgs.forEach(this::addArgumentMessage);
        return this;
    }

    protected BaseFunctionField addArgumentMessage(ErrorMessage msg) {
        ErrorMessage copy = msg.copy();
        if(copy.getPath().isEmpty()) {
            copy.setPath(path());
        }

        report.addArgumentMessage(copy);
        return this;
    }

    protected BaseFunctionField addResultMessages(List<ErrorMessage> msgs) {
        msgs.forEach(this::addResultMessage);
        return this;
    }

    protected BaseFunctionField addResultMessage(ErrorMessage msg) {
        ErrorMessage copy = msg.copy();
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
        return addArgumentMessages(report.argumentMessages()).
                addResultMessages(report.resultMessages());
    }

    private String formatErrorCodes(Set<String> errorCodes) {
        List sortedErrors = new ArrayList<>(errorCodes);
        Collections.sort(sortedErrors);
        return String.join(",    \r\n", sortedErrors);
    }
}
