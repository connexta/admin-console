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
package org.codice.ddf.admin.sources.utils;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;

public class SourceTaskCallable<T> implements Callable<ReportWithResultImpl<T>> {

  private String urlFormatString;

  private HostField host;

  private CredentialsField creds;

  private BiFunction<UrlField, CredentialsField, ReportWithResultImpl<T>> function;

  public SourceTaskCallable(
      String urlFormatString,
      HostField host,
      CredentialsField creds,
      BiFunction<UrlField, CredentialsField, ReportWithResultImpl<T>> function) {
    this.urlFormatString = urlFormatString;
    this.host = host;
    this.creds = creds;
    this.function = function;
  }

  @Override
  public ReportWithResultImpl<T> call() throws Exception {
    UrlField requestUrl = new UrlField();
    String formattedUrl = String.format(urlFormatString, host.hostname(), host.port());
    requestUrl.setValue(formattedUrl);

    ReportWithResultImpl<T> configResult = function.apply(requestUrl, creds);

    if (!configResult.containsErrorMsgs()) {
      return configResult;
    }

    return null;
  }
}
