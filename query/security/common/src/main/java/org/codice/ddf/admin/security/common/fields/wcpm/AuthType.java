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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;
import org.codice.ddf.security.handler.api.AuthenticationHandler;

public class AuthType extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "authType";

    public static final String FIELD_TYPE_NAME = "AuthenticationType";

    public static final String DESCRIPTION =
            "Defines a specific type of authentication that should be performed.";

    private ServiceReader serviceReader;

    public AuthType(ServiceReader serviceReader) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION);
        this.serviceReader = serviceReader;
    }

    @Override
    public List<EnumValue<String>> getEnumValues() {
        return serviceReader.getServices(AuthenticationHandler.class, null).stream().map(handler -> new EnumValue<String>() {
            @Override
            public String enumTitle() {
                return handler.getAuthenticationType();
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String value() {
                return handler.getAuthenticationType();
            }
        }).collect(Collectors.toList());
    }

    public static class AuthTypes extends BaseListField<AuthType> {

        public static final String DEFAULT_FIELD_NAME = "authTypes";

        private Callable<AuthType> newAuthType;

        private ServiceReader serviceReader;

        public AuthTypes(ServiceReader serviceReader) {
            super(DEFAULT_FIELD_NAME);
            this.serviceReader = serviceReader;
            newAuthType = () -> new AuthType(serviceReader);
        }

        @Override
        public Callable<AuthType> getCreateListEntryCallable() {
            return newAuthType;
        }

        public AuthTypes useDefaultIsRequired(){
            newAuthType =  () -> {
                AuthType authType = new AuthType(serviceReader);
                authType.isRequired(true);
                return authType;
            };

            isRequired(true);
            return this;
        }

        @Override
        public AuthTypes addAll(Collection<AuthType> values) {
            super.addAll(values);
            return this;
        }
    }

}
