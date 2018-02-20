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
package org.codice.ddf.admin.api.report;

import java.util.List;

/**
 * A report contains the result of an operation or error messages associated with the execution of
 * the operation. A Report can contain only errors messages or a result, but not both.
 */
public interface Report<T> {

  // TODO: 2/20/18 phuffer - Consider removing builder pattern for all these methods
  /**
   * Adds an {@link ErrorMessage} to this report.
   *
   * @param message the message to add
   * @return this report
   */
  Report<T> addErrorMessage(ErrorMessage message);

  /**
   * Copies the error messages from the given {@code report}.
   *
   * @param report report to copy error messages from
   * @return this report
   */
  Report<T> addErrorMessages(Report<?> report);

  // TODO: 2/20/18 phuffer - Consider returning Set<ErrorMessage>
  /**
   * @return the error messages inside this report
   */
  List<ErrorMessage> getErrorMessages();

  /**
   * @return true if there are errors; otherwise, false
   */
  boolean containsErrorMessages();

  /**
   * @return the result of this report, or null if there are errors
   */
  T getResult();

  /**
   * @param result the result of this report
   * @return this report
   */
  Report<T> setResult(T result);

  /**
   * @return true if this report has a result; otherwise, false
   */
  boolean isResultPresent();
}
