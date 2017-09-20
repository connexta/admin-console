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
package org.codice.ddf.admin.common.report;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.api.report.Report;

public class ReportImpl<T> implements Report<T> {

  private List<ErrorMessage> errorMessages;

  private Optional<T> result;

  public ReportImpl() {
    errorMessages = new ArrayList<>();
    this.result = Optional.empty();
  }

  public ReportImpl(T result) {
    this();
    this.result = Optional.of(result);
  }

  public ReportImpl(ErrorMessage message) {
    this();
    errorMessages.add(message);
  }

  public ReportImpl(List<ErrorMessage> messages) {
    this();
    errorMessages.addAll(messages);
  }

  @Override
  public Report<T> addErrorMessage(ErrorMessage message) {
    errorMessages.add(message);
    return this;
  }

  @Override
  public <S> Report<T> addErrorMessages(Report<S> report) {
    errorMessages.addAll(report.getErrorMessages());
    return this;
  }

  @Override
  public List<ErrorMessage> getErrorMessages() {
    return ImmutableList.copyOf(errorMessages);
  }

  @Override
  public boolean containsErrorMessages() {
    return !errorMessages.isEmpty();
  }

  @Override
  public T getResult() {
    return result.orElse(null);
  }

  @Override
  public Report<T> setResult(T result) {
    this.result = Optional.ofNullable(result);
    return this;
  }

  @Override
  public boolean isResultPresent() {
    return result.isPresent();
  }
}
