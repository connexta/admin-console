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
package org.codice.ddf.admin.security.wcpm.discover;

import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.security.common.fields.wcpm.Realm;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class GetRealms extends GetFunctionField<Realm.Realms> {

    public static final String FIELD_NAME = "realms";

    public static final String DESCRIPTION = "Retrieves all currently configured realms.";

    private ServiceReader serviceReader;

    public GetRealms(ServiceReader serviceReader) {
        super(FIELD_NAME, DESCRIPTION);
        this.serviceReader = serviceReader;
    }

    @Override
    public Realm.Realms performFunction() {
        List<Realm> realms = new Realm(serviceReader).getEnumValues()
                .stream()
                .map(enumVal -> new Realm(serviceReader, enumVal))
                .collect(Collectors.toList());

        return new Realm.Realms(serviceReader).addAll(realms);
    }

    @Override
    public Realm.Realms getReturnType() {
        return new Realm.Realms(serviceReader);
    }

    @Override
    public FunctionField<Realm.Realms> newInstance() {
        return new GetRealms(serviceReader);
    }
}
