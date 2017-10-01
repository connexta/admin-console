/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.common.fields.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.report.FunctionReportImpl;
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;

public abstract class BaseFunctionField<T extends Field> implements FunctionField<T> {

  private FunctionReportImpl<T> report;

  private String name;

  private String description;

  private List<Object> path;

  public BaseFunctionField(String name, String description) {
    this.name = name;
    this.description = description;
    path = new ArrayList<>();
    report = new FunctionReportImpl<>();
  }

  @Override
  public String getFunctionName() {
    return name;
  }

  @Override
  public String getDescription() {
    Set<String> errors = getErrorCodes();
    if (!errors.isEmpty()) {
      return String.format(
          "%s %n%n The possible errors are: %n- %s", description, formatErrorCodes(errors));
    }
    return description;
  }

  public abstract T performFunction();

  public abstract Set<String> getFunctionErrorCodes();

  @Override
  public Set<String> getErrorCodes() {
    Set<String> errorCodes = new HashSet<>();
    for (Field field : getArguments()) {
      errorCodes.addAll(field.getErrorCodes());
    }
    return new ImmutableSet.Builder<String>()
        .addAll(getFunctionErrorCodes())
        .addAll(errorCodes)
        .build();
  }

  protected void setArguments(Map<String, Object> args) {
    if (args == null || args.isEmpty()) {
      return;
    }

    getArguments()
        .stream()
        .filter(field -> args.containsKey(field.getFieldName()))
        .forEach(field -> field.setValue(args.get(field.getFieldName())));
  }

  @Override
  public FunctionReport<T> execute(Map<String, Object> args, List<Object> functionPath) {
    setArguments(args);
    setPath(functionPath);
    validate();
    if (!report.containsErrorMessages()) {
      report.setResult(performFunction());
    }

    return report;
  }

  @Override
  public List<Object> getPath() {
    return new ImmutableList.Builder<>().addAll(path).build();
  }

  protected void setPath(List<Object> path) {
    if (path == null) {
      return;
    }
    this.path.clear();
    this.path.addAll(path);
    getArguments()
        .forEach(
            arg ->
                arg.setPath(
                    new ImmutableList.Builder<>()
                        .addAll(getPath())
                        .add(arg.getFieldName())
                        .build()));
  }

  public void validate() {
    getArguments()
        .stream()
        .map(Field::validate)
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
    if (message.getPath().isEmpty()) {
      message.setPath(getPath());
    }

    report.addErrorMessage(message);
    return this;
  }

  protected BaseFunctionField addErrorMessages(Report report) {
    return addErrorMessages(report.getErrorMessages());
  }

  private String formatErrorCodes(Set<String> errorCodes) {
    return errorCodes.stream().sorted().collect(Collectors.joining("\n- "));
  }
}
