package org.codice.ddf.admin.query.wcpm.sample;

import org.codice.ddf.admin.query.commons.fields.common.ContextPath;
import org.codice.ddf.admin.query.commons.fields.common.ContextPaths;
import org.codice.ddf.admin.query.wcpm.fields.AuthType;
import org.codice.ddf.admin.query.wcpm.fields.AuthTypeList;
import org.codice.ddf.admin.query.wcpm.fields.ClaimsMapEntry;
import org.codice.ddf.admin.query.wcpm.fields.ContextPolicies;
import org.codice.ddf.admin.query.wcpm.fields.ContextPolicyBin;
import org.codice.ddf.admin.query.wcpm.fields.Realm;
import org.codice.ddf.admin.query.wcpm.fields.RealmList;

public class SampleFields {

    public static final AuthTypeList SAMPLE_AUTH_TYPES_LIST = new AuthTypeList()
            .add(AuthType.BASIC_AUTH)
            .add(AuthType.IDP_AUTH)
            .add(AuthType.PKI_AUTH)
            .add(AuthType.SAML_AUTH)
            .add(AuthType.GUEST_AUTH);

    public static final RealmList SAMPLE_REALM_LIST = new RealmList()
            .add(Realm.KARAF_REALM)
            .add(Realm.LDAP_REALM);

    public static final ContextPath SAMPLE_CONTEXT_PATH = new ContextPath();
    static {
        SAMPLE_CONTEXT_PATH.setValue("/example/path");
    }

    public static final ContextPaths SAMPLE_CONTEXT_PATHS = new ContextPaths()
            .add(SAMPLE_CONTEXT_PATH)
            .add(SAMPLE_CONTEXT_PATH)
            .add(SAMPLE_CONTEXT_PATH);

    public static final ClaimsMapEntry SAMPLE_CLAIMS_MAP_ENTRY = new ClaimsMapEntry()
            .claim("sampleClaim").claimValue("sampleClaimValue");

    public static final ContextPolicyBin SAMPLE_CONTEXT_POLICY_BIN = new ContextPolicyBin()
            .realm(Realm.KARAF_REALM)
            .addContextPath(SAMPLE_CONTEXT_PATH)
            .addContextPath(SAMPLE_CONTEXT_PATH)
            .addAuthType(AuthType.BASIC_AUTH)
            .addAuthType(AuthType.IDP_AUTH)
            .addClaimsMapping(SAMPLE_CLAIMS_MAP_ENTRY)
            .addClaimsMapping(SAMPLE_CLAIMS_MAP_ENTRY)
            .addClaimsMapping(SAMPLE_CLAIMS_MAP_ENTRY);

    public static final ContextPolicies SAMPLE_CONTEXT_POLICES = new ContextPolicies().add(
            SAMPLE_CONTEXT_POLICY_BIN)
            .add(SAMPLE_CONTEXT_POLICY_BIN);

}
