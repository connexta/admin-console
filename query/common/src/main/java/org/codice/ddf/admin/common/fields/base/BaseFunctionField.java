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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Handles converting arguments to field arguments, validation of arguments, and attaching {@link
 * ErrorMessage} paths to appropriate arguments or the function itself.
 *
 * @param <T> the return type
 */
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

  /**
   * Method that will be called by the {@link #execute(Map, List)} method after arguments have
   * successfully been set and validated.
   *
   * @return result of the function. If errors exist in this {@link FunctionField}'s {@link
   *     FunctionReport}, the result will be ignored.
   */
  public abstract T performFunction();

  /**
   * Possible error codes that can be returned by {@link FunctionField}s extending this class. This
   * {@link FunctionField}'s argument error codes should not be included.
   *
   * @return error codes
   */
  public abstract Set<String> getFunctionErrorCodes();

  /** @return the union of this function and its arguments' error codes. */
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

  /**
   * First sets the arguments of this {@link FunctionField}, then sets all the arguments' paths,
   * then validates the arguments, and finally calls {@link #performFunction()} if there are no
   * argument validation errors.
   *
   * @param args a map containing argument names to argument values
   * @param functionPath
   * @return report of the function
   */
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

  /**
   * Calls {@link Field#validate()} on all this {@link FunctionField}'s arguments. To do function
   * specific validation (for example, cross argument validation). Typically, extending classes that
   * override this method should call {@code super.validate()}.
   */
  public void validate() {
    getArguments()
        .stream()
        .map(Field::validate)
        // Deleting the Collection type causes errors
        .flatMap(Collection<ErrorMessage>::stream)
        .forEach(this::addErrorMessage);
  }

  /**
   * Useful for sub-classes to check if they want to halt processing early.
   *
   * @return {@code true} if the function returns errors, otherwise {@code false}
   */
  protected boolean containsErrorMsgs() {
    return report.containsErrorMessages();
  }

  /**
   * Adds list of error message to this {@link FunctionField}s.
   *
   * @param msgs list of {@link ErrorMessage}s to add
   * @return
   */
  // TODO: phuffer - Remove this fluent builder
  protected BaseFunctionField addErrorMessages(List<ErrorMessage> msgs) {
    msgs.forEach(this::addErrorMessage);
    return this;
  }

  /**
   * Add an error message to this {@link FunctionField}s.
   *
   * @param msg {@link ErrorMessage} to add
   * @return
   */
  // TODO: phuffer - Remove this fluent builder
  protected BaseFunctionField addErrorMessage(ErrorMessage msg) {
    ErrorMessage message = new ErrorMessageImpl(msg.getCode(), msg.getPath());
    if (message.getPath().isEmpty()) {
      message.setPath(getPath());
    }

    report.addErrorMessage(message);
    return this;
  }

  /**
   * Adds {@link ErrorMessage}s from an existing {@link Report}
   *
   * @param report {@link Report} to copy {@link ErrorMessage}s from
   * @return
   */
  // TODO: phuffer - Remove this fluent builder
  protected BaseFunctionField addErrorMessages(Report report) {
    return addErrorMessages(report.getErrorMessages());
  }

  private void setPath(List<Object> path) {
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

  // TODO: phuffer - consider validating argument value types here
  private void setArguments(Map<String, Object> args) {
    if (args == null || args.isEmpty()) {
      return;
    }

    getArguments()
        .stream()
        .filter(field -> args.containsKey(field.getFieldName()))
        .forEach(field -> field.setValue(args.get(field.getFieldName())));
  }

  private String formatErrorCodes(Set<String> errorCodes) {
    return errorCodes.stream().sorted().collect(Collectors.joining("\n- "));
  }
}
