package org.codice.ddf.admin.query.wcpm.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;
import org.codice.ddf.admin.query.commons.fields.common.ContextPathField;
import org.codice.ddf.admin.query.commons.fields.common.ContextPathList;

import com.google.common.collect.ImmutableList;

public class ContextPolicyBin extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "bin";
    public static final String FIELD_TYPE_NAME  ="ContextPolicyBin";
    public static final String DESCRIPTION = "Represents a policy being applied to a set of context paths.";

    private ContextPathList contexts;
    private AuthTypeList authTypes;
    private Realm realm;
    private ClaimsMapping claimsMapping;

    public ContextPolicyBin() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        contexts = new ContextPathList();
        authTypes = new AuthTypeList();
        realm = new Realm();
        claimsMapping = new ClaimsMapping();
    }

    public ContextPolicyBin realm(StringField realm) {
        this.realm.setValue(realm);
        return this;
    }

    public ContextPolicyBin addContextPath(ContextPathField contextPath) {
        contexts.addField(contextPath);
        return this;
    }

    public ContextPolicyBin addClaimsMapping(ClaimsMapEntry entry) {
        claimsMapping.addField(entry);
        return this;
    }

    public ContextPolicyBin addAuthType(StringField authType) {
        authTypes.addField(authType);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(contexts, authTypes, realm, claimsMapping);
    }
}
