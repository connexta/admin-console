package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class AuthType extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "authType";
    public static final String FIELD_TYPE_NAME = "AuthenticationType";
    public static final String DESCRIPTION = "Defines a specific type of authentication that should be performed.";

    public static final AuthType BASIC_AUTH = new AuthType(new BasicAuth());
    public static final AuthType SAML_AUTH = new AuthType(new SamlAuth());
    public static final AuthType PKI_AUTH = new AuthType(new PkiAuth());
    public static final AuthType IDP_AUTH = new AuthType(new IdpAuth());
    public static final AuthType GUEST_AUTH = new AuthType(new GuestAuth());

    public AuthType() {
        this(null);
    }

    protected AuthType(Field<String> authType) {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION, ImmutableList.of(new BasicAuth(), new SamlAuth(), new PkiAuth(), new IdpAuth(), new GuestAuth()), authType);
    }

    protected static final class BasicAuth extends StringField {
        public static final String BASIC = "basic";
        public static final String FIELD_NAME = BASIC;
        public static final String FIELD_TYPE = BASIC;
        public static final String DESCRIPTION = "Basic access authentication is a method for a HTTP user agent to provide a user name and password when making a request.";

        public BasicAuth() {
            super(FIELD_NAME, FIELD_TYPE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return BASIC;
        }
    }

    protected static final class SamlAuth extends StringField {
        public static final String SAML = "SAML";
        public static final String FIELD_NAME = SAML;
        public static final String FIELD_TYPE = SAML;
        public static final String DESCRIPTION = "Security Assertion Markup Language is an XML-based, open-standard data format for exchanging authentication and authorization data between parties, in particular, between an identity provider and a service provider.";

        public SamlAuth() {
            super(FIELD_NAME, FIELD_TYPE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return SAML;
        }
    }

    protected static final class PkiAuth extends StringField {
        public static final String PKI = "PKI";
        public static final String FIELD_NAME = PKI;
        public static final String FIELD_TYPE_ = PKI;
        public static final String DESCRIPTION = "A public key infrastructure (PKI) is a set of roles, policies, and procedures needed to create, manage, distribute, use, store, and revoke digital certificates and manage public-key encryption.";

        public PkiAuth() {
            super(FIELD_NAME, FIELD_TYPE_, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return PKI;
        }
    }

    protected static final class IdpAuth extends StringField {
        public static final String IDP = "IdP";
        public static final String FIELD_NAME = IDP;
        public static final String FIELD_TYPE_ = IDP;
        public static final String DESCRIPTION = "Identity provider (IdP), also known as Identity Assertion Provider. Activates SAML Web SSO authentication support.";

        public IdpAuth() {
            super(FIELD_NAME, FIELD_TYPE_, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return IDP;
        }
    }

    protected static final class GuestAuth extends StringField {
        public static final String GUEST = "guest";
        public static final String FIELD_NAME = GUEST;
        public static final String FIELD_TYPE_ = GUEST;
        public static final String DESCRIPTION = "Provides guest access.";

        public GuestAuth() {
            super(FIELD_NAME, FIELD_TYPE_, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return GUEST;
        }
    }

}
