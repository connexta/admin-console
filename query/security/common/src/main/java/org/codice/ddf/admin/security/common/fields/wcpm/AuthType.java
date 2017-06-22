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

import java.util.Collection;
import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.BaseListField;

import com.google.common.collect.ImmutableList;

public class AuthType extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "authType";

    public static final String FIELD_TYPE_NAME = "AuthenticationType";

    public static final String DESCRIPTION =
            "Defines a specific type of authentication that should be performed.";

    public static final AuthType BASIC_AUTH = new AuthType(new BasicAuth());

    public static final AuthType SAML_AUTH = new AuthType(new SamlAuth());

    public static final AuthType PKI_AUTH = new AuthType(new PkiAuth());

    public static final AuthType IDP_AUTH = new AuthType(new IdpAuth());

    public static final AuthType GUEST_AUTH = new AuthType(new GuestAuth());

    // TODO: tbatie - 6/21/17 - Should only be once constructor when this is finished
    public AuthType() {
        this(null);
    }

    public AuthType(EnumValue<String> authType) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new BasicAuth(),
                        new SamlAuth(),
                        new PkiAuth(),
                        new IdpAuth(),
                        new GuestAuth()),
                authType);
    }

    public static final class BasicAuth implements EnumValue<String> {
        public static final String BASIC = "basic";

        public static final String DESCRIPTION =
                "Basic access authentication is a method for a HTTP user agent to provide a user name and password when making a request.";

        @Override
        public String enumTitle() {
            return BASIC;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return BASIC;
        }
    }

    public static final class SamlAuth implements EnumValue<String> {

        public static final String SAML = "SAML";

        public static final String DESCRIPTION =
                "Security Assertion Markup Language is an XML-based, open-standard data format for exchanging authentication and authorization data between parties, in particular, between an identity provider and a service provider.";

        @Override
        public String enumTitle() {
            return SAML;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return SAML;
        }
    }

    public static final class PkiAuth implements EnumValue<String> {
        public static final String PKI = "PKI";

        public static final String DESCRIPTION =
                "A public key infrastructure (PKI) is a set of roles, policies, and procedures needed to create, manage, distribute, use, store, and revoke digital certificates and manage public-key encryption.";

        @Override
        public String enumTitle() {
            return PKI;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return PKI;
        }
    }

    public static final class IdpAuth implements EnumValue<String> {

        public static final String IDP = "IdP";

        public static final String DESCRIPTION =
                "Identity provider (IdP), also known as Identity Assertion Provider. Activates SAML Web SSO authentication support.";

        @Override
        public String enumTitle() {
            return IDP;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return IDP;
        }
    }

    public static final class GuestAuth implements EnumValue<String> {
        public static final String GUEST = "guest";

        public static final String DESCRIPTION = "Provides guest access.";

        @Override
        public String enumTitle() {
            return GUEST;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return GUEST;
        }
    }

    public static class AuthTypes extends BaseListField<AuthType> {

        public static final String DEFAULT_FIELD_NAME = "authTypes";

        private Callable<AuthType> newAuthType;

        public AuthTypes() {
            super(DEFAULT_FIELD_NAME);
            newAuthType = AuthType::new;
        }

        @Override
        public Callable<AuthType> getCreateListEntryCallable() {
            return newAuthType;
        }

        public AuthTypes useDefaultIsRequired(){
            newAuthType =  () -> {
                AuthType authType = new AuthType();
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
