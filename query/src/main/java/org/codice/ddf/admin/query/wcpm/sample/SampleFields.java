package org.codice.ddf.admin.query.wcpm.sample;

import org.codice.ddf.admin.query.commons.fields.common.ContextPathField;
import org.codice.ddf.admin.query.commons.fields.common.ContextPathList;
import org.codice.ddf.admin.query.wcpm.fields.AuthType;
import org.codice.ddf.admin.query.wcpm.fields.AuthTypeList;
import org.codice.ddf.admin.query.wcpm.fields.ClaimsMapEntry;
import org.codice.ddf.admin.query.wcpm.fields.ContextPolicies;
import org.codice.ddf.admin.query.wcpm.fields.ContextPolicyBin;
import org.codice.ddf.admin.query.wcpm.fields.Realm;
import org.codice.ddf.admin.query.wcpm.fields.RealmList;

public class SampleFields {

    public static final AuthTypeList SAMPLE_AUTH_TYPES_LIST = new AuthTypeList()
            .addField(AuthType.BASIC_AUTH)
            .addField(AuthType.IDP_AUTH)
            .addField(AuthType.PKI_AUTH)
            .addField(AuthType.SAML_AUTH)
            .addField(AuthType.GUEST_AUTH);

    public static final RealmList SAMPLE_REALM_LIST = new RealmList()
            .addField(Realm.KARAF_REALM)
            .addField(Realm.LDAP_REALM);

    public static final ContextPathField SAMPLE_CONTEXT_PATH = new ContextPathField().setValue("/example/path");

    public static final ContextPathList SAMPLE_CONTEXT_PATH_LIST = new ContextPathList()
            .addField(SAMPLE_CONTEXT_PATH)
            .addField(SAMPLE_CONTEXT_PATH)
            .addField(SAMPLE_CONTEXT_PATH)
            .addField(SAMPLE_CONTEXT_PATH);

    public static final ClaimsMapEntry SAMPLE_CLAIMS_MAP_ENTRY = new ClaimsMapEntry().claim(
            "sampleClaim")
            .claimValue("sampleClaimValue");

    public static final ContextPolicyBin SAMPLE_CONTEXT_POLICY_BIN = new ContextPolicyBin()
            .realm(Realm.KARAF_REALM)
            .addContextPath(SAMPLE_CONTEXT_PATH)
            .addContextPath(SAMPLE_CONTEXT_PATH)
            .addAuthType(AuthType.BASIC_AUTH)
            .addAuthType(AuthType.IDP_AUTH)
            .addClaimsMapping(SAMPLE_CLAIMS_MAP_ENTRY)
            .addClaimsMapping(SAMPLE_CLAIMS_MAP_ENTRY)
            .addClaimsMapping(SAMPLE_CLAIMS_MAP_ENTRY);

    public static final ContextPolicies SAMPLE_CONTEXT_POLICES = new ContextPolicies().addField(
            SAMPLE_CONTEXT_POLICY_BIN)
            .addField(SAMPLE_CONTEXT_POLICY_BIN);

}
