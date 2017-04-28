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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.duplicateMapKeyError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;

import com.google.common.collect.ImmutableList;

public class MapField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "map";

    public static final String FIELD_TYPE_NAME = "Map";

    public static final String DESCRIPTION = "A map containing a list of key value pairs.";

    public static final String ENTRIES_FIELD_NAME = "entries";

    private ListField<PairField> entries;

    public MapField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        entries = new ListFieldImpl<>(ENTRIES_FIELD_NAME, PairField.class);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(entries);
    }

    public MapField put(String key, String value) {
        if (containsKey(key)) {
            entries.getList()
                    .stream()
                    .filter(p -> p.key()
                            .equals(key))
                    .findFirst()
                    .ifPresent(pair -> pair.value(value));
        } else {
            entries.add(new PairField().key(key)
                    .value(value));
        }
        return this;
    }

    public boolean containsValue(String value) {
        return entries.getList()
                .stream()
                .anyMatch(pair -> pair.value()
                        .equals(value));
    }

    public boolean containsKey(String key) {
        return entries.getList()
                .stream()
                .anyMatch(pair -> pair.key()
                        .equals(key));
    }

    public Optional<EntryField> getEntry(String key) {
        return entries.getList()
                .stream()
                .filter(entry -> entry.key()
                        .equals(key))
                .findFirst();
    }
    public boolean isEmpty() {
        return entries.getList()
                .isEmpty();
    }

    @Override
    public List<ErrorMessage> validate() {
        List<ErrorMessage> validationMsgs = super.validate();
        if (!validationMsgs.isEmpty()) {
            return validationMsgs;
        }

        for (PairField toMatch : entries.getList()) {
            List<PairField> duplicatePairs = entries.getList()
                    .stream()
                    .filter(pair -> pair.key()
                            .equals(toMatch.key()))
                    .collect(Collectors.toList());

            if (duplicatePairs.size() > 1) {
                validationMsgs.add(duplicateMapKeyError(duplicatePairs.get(1)
                        .path()));
                break;
            }
        }
        return validationMsgs;
    }
}
