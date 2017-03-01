package org.codice.ddf.admin.query.wcpm.fields;

import java.util.List;

import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class AuthType extends BaseEnumField<StringField> {

    public static final String DEFAULT_FIELD_NAME = "authType";
    public static final String FIELD_TYPE_NAME = "AuthenticationType";
    public static final String DESCRIPTION = "Defines a specific type of authentication that should be performed.";

    public static final StringField BASIC_AUTH = new BasicAuth();
    public static final StringField SAML_AUTH = new SamlAuth();
    public static final StringField PKI_AUTH = new PkiAuth();
    public static final StringField IDP_AUTH = new IdpAuth();
    public static final StringField GUEST_AUTH = new GuestAuth();

    private static final List<StringField> AUTHENTICATION_TYPES = ImmutableList.of(BASIC_AUTH, SAML_AUTH, PKI_AUTH, IDP_AUTH, GUEST_AUTH);

    public AuthType() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<StringField> getEnumValues() {
        return AUTHENTICATION_TYPES;
    }

    protected static final class BasicAuth extends StringField {
        public static final String BASIC = "basic";
        public static final String FIELD_NAME = BASIC;
        public static final String FIELD_TYPE_ = BASIC;
        public static final String DESCRIPTION = "Baasic access authentication is a method for a HTTP user agent to provide a user name and password when making a request.";

        public BasicAuth() {
            super(FIELD_NAME, FIELD_TYPE_, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return BASIC;
        }
    }

    protected static final class SamlAuth extends StringField {
        public static final String SAML = "SAML";
        public static final String FIELD_NAME = SAML;
        public static final String FIELD_TYPE_ = SAML;
        public static final String DESCRIPTION = "Security Assertion Markup Language is an XML-based, open-standard data format for exchanging authentication and authorization data between parties, in particular, between an identity provider and a service provider.";

        public SamlAuth() {
            super(FIELD_NAME, FIELD_TYPE_, DESCRIPTION);
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
