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

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.karaf.jaas.config.JaasRealm;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class Realm extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "realm";

    public static final String FIELD_TYPE_NAME = "Realm";

    public static final String DESCRIPTION =
            "Authenticating Realms are used to authenticate an incoming authentication token and create a Subject on successful authentication.";

    private ServiceReader serviceReader;

    public Realm(ServiceReader serviceReader) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION);
        this.serviceReader = serviceReader;
    }

    @Override
    public List<EnumValue<String>> getEnumValues() {
        Set<JaasRealm> realms = serviceReader.getServices(JaasRealm.class, null);
        return realms.stream().map(realm -> new EnumValue<String>() {
            @Override
            public String enumTitle() {
                return realm.getName();
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String value() {
                return realm.getName();
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Realm isRequired(boolean required) {
        super.isRequired(required);
        return this;
    }

    public static class Realms extends BaseListField<Realm> {

        public static final String DEFAULT_FIELD_NAME = "realms";

        private ServiceReader serviceReader;

        public Realms(ServiceReader serviceReader) {
            super(DEFAULT_FIELD_NAME);
            this.serviceReader = serviceReader;
        }

        @Override
        public Callable<Realm> getCreateListEntryCallable() {
            return () -> new Realm(serviceReader);
        }
    }
}