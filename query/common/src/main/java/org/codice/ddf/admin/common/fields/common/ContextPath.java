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
package org.codice.ddf.admin.common.fields.common;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.invalidContextPathError;

import java.util.List;

import org.apache.commons.validator.UrlValidator;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class ContextPath extends StringField {

    public static final String DEFAULT_FIELD_NAME = "path";

    public static final String FIELD_TYPE_NAME = "ContextPath";

    public static final String DESCRIPTION =
            "The context path is the suffix of a URL path that is used to select the context(s) to which an incoming request is passed. For example, http://hostname.com/<contextPath>.";

    public ContextPath() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public ContextPath(String path) {
        this();
        setValue(path);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    public List<ErrorMessage> validate() {
        List<ErrorMessage> msgs = super.validate();
        if (!msgs.isEmpty()) {
            return msgs;
        } else if (getValue() != null && !getValue().isEmpty()) {
            final UriPathValidator validator = new UriPathValidator();
            if (!validator.isValidPath(getValue())) {
                msgs.add(invalidContextPathError(path()));
            }
        }

        return msgs;
    }

    @Override
    public ContextPath isRequired(boolean required) {
        super.isRequired(required);
        return this;
    }

    private static class UriPathValidator extends UrlValidator {
        @Override
        protected boolean isValidPath(String path) {
            return super.isValidPath(path);
        }
    }
}
