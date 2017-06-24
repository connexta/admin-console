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
package org.codice.ddf.admin.security.common.fields.wcpm;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.missingKeyValue;

import java.util.List;
import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class ClaimsMapEntry extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "claimsMapping";

    public static final String FIELD_TYPE_NAME = "ClaimsMapEntry";

    public static final String DESCRIPTION =
            "Represents a mapping of a key subject to a specific key value";

    public static final String KEY_FIELD_NAME = "key";

    public static final String VALUE_FIELD_NAME = "value";

    private StringField key;

    private StringField value;

    public ClaimsMapEntry() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        key = new StringField(KEY_FIELD_NAME);
        value = new StringField(VALUE_FIELD_NAME);
        updateInnerFieldPaths();
    }

    public ClaimsMapEntry key(String key) {
        this.key.setValue(key);
        return this;
    }

    public ClaimsMapEntry value(String value) {
        this.value.setValue(value);
        return this;
    }

    public String key() {
        return key.getValue();
    }

    public String value() {
        return value.getValue();
    }

    public StringField claimField() {
        return key;
    }

    public StringField claimValueField() {
        return value;
    }

    @Override
    public ClaimsMapEntry isRequired(boolean required) {
        super.isRequired(required);
        return this;
    }

    @Override
    public List<ErrorMessage> validate() {
        List validationMsgs = super.validate();
        if (!validationMsgs.isEmpty()) {
            return validationMsgs;
        }

        if (key.getValue() != null) {
            if (value.getValue() == null || value.getValue()
                    .isEmpty()) {
                validationMsgs.add(missingKeyValue(value.path()));
            }
        }

        return validationMsgs;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(key, value);
    }

    public static class ListImpl extends BaseListField<ClaimsMapEntry> {

        public static final String DEFAULT_FIELD_NAME = "claimsMapping";

        private Callable<ClaimsMapEntry> newClaimsEntry;

        public ListImpl() {
            super(DEFAULT_FIELD_NAME);
            newClaimsEntry = ClaimsMapEntry::new;
        }

        public ListImpl(String fieldName) {
            super(fieldName);
            newClaimsEntry = ClaimsMapEntry::new;
        }

        @Override
        public Callable<ClaimsMapEntry> getCreateListEntryCallable() {
            return newClaimsEntry;
        }

        @Override
        public ListImpl useDefaultRequired() {
            newClaimsEntry = () -> {
                ClaimsMapEntry entry = new ClaimsMapEntry();
                entry.claimField().isRequired(true);
                entry.claimValueField().isRequired(true);
                return entry;
            };

            return this;
        }
    }
}
