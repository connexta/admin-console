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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.report.FunctionReportImpl;
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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
        Set<String> errors = getErrorCodes();
        if (!errors.isEmpty()) {
            return String.format("%s %n%n The possible errors are: %n- %s",
                    description,
                    formatErrorCodes(errors));
        }
        return super.description();
    }

    public abstract T performFunction();

    public abstract Set<String> getFunctionErrorCodes();

    @Override
    public Set<String> getErrorCodes() {
        Set<String> errorCodes = new HashSet<>();
        for (DataType dataType : getArguments()) {
            errorCodes.addAll(dataType.getErrorCodes());
        }
        return new ImmutableSet.Builder<String>()
                .addAll(getFunctionErrorCodes())
                .addAll(errorCodes)
                .build();
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
        if (!report.containsErrorMessages()) {
            report.setResult(performFunction());
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
                .forEach(this::addErrorMessage);
    }

    protected boolean containsErrorMsgs() {
        return report.containsErrorMessages();
    }

    protected BaseFunctionField addErrorMessages(List<ErrorMessage> msgs) {
        msgs.forEach(this::addErrorMessage);
        return this;
    }

    protected BaseFunctionField addErrorMessage(ErrorMessage msg) {
        ErrorMessage message = new ErrorMessageImpl(msg.getCode(), msg.getPath());
        if(message.getPath().isEmpty()) {
            message.setPath(path());
        }

        report.addErrorMessage(message);
        return this;
    }

    protected BaseFunctionField addReportMessages(Report report) {
        return addErrorMessages(report.getErrorMessages());
    }

    private String formatErrorCodes(Set<String> errorCodes) {
        return errorCodes.stream()
                .sorted()
                .collect(Collectors.joining("\n- "));
    }
}
