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
package org.codice.ddf.admin.common.fields.common;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class ResponseField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "response";

  public static final String FIELD_TYPE_NAME = "Response";

  public static final String DESCRIPTION =
      "Represents an HTTP response containing the status code, response body, and request URL.";

  public static final String STATUS_CODE_FIELD_NAME = "statusCode";

  public static final String RESPONSE_BODY_FIELD_NAME = "responseBody";

  public static final String REQUEST_URL_FIELD_NAME = "requestUrl";

  public static final String CONTENT_TYPE_FIELD_NAME = "contentType";

  private IntegerField statusCode;

  private StringField responseBody;

  private UrlField requestUrl;

  private StringField contentType;

  public ResponseField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    statusCode = new IntegerField(STATUS_CODE_FIELD_NAME);
    responseBody = new StringField(RESPONSE_BODY_FIELD_NAME);
    requestUrl = new UrlField(REQUEST_URL_FIELD_NAME);
    contentType = new StringField(CONTENT_TYPE_FIELD_NAME);
  }

  public ResponseField statusCode(Integer statusCode) {
    this.statusCode.setValue(statusCode);
    return this;
  }

  public ResponseField responseBody(String body) {
    this.responseBody.setValue(body);
    return this;
  }

  public ResponseField requestUrl(String requestUrl) {
    this.requestUrl.setValue(requestUrl);
    return this;
  }

  public ResponseField contentType(String contentType) {
    this.contentType.setValue(contentType);
    return this;
  }

  public Integer statusCode() {
    return statusCode.getValue();
  }

  public String responseBody() {
    return responseBody.getValue();
  }

  public String requestUrl() {
    return requestUrl.getValue();
  }

  public String contentType() {
    return contentType.getValue();
  }

  public IntegerField statusCodeField() {
    return statusCode;
  }

  public StringField responseBodyField() {
    return responseBody;
  }

  public UrlField requestUrlField() {
    return requestUrl;
  }

  public StringField contentTypeField() {
    return contentType;
  }

  public ResponseField requestUrlField(UrlField requestUrl) {
    this.requestUrl = requestUrl;
    return this;
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(statusCode, responseBody, requestUrl, contentType);
  }
}
