package org.codice.ddf.admin.query.wcpm.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class ClaimsMapEntry extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "claimsMapping";
    public static final String FIELD_TYPE_NAME = "ClaimsMapEntry";
    public static final String DESCRIPTION = "Represents a mapping of a claim subject to a specific claim value";

    private StringField claim;
    private StringField claimValue;

    public ClaimsMapEntry() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        claim = new StringField("claim");
        claimValue = new StringField("claimValue");
    }

    public ClaimsMapEntry claim(String claim) {
        this.claim.setValue(claim);
        return this;
    }

    public ClaimsMapEntry claimValue(String claimValue) {
        this.claimValue.setValue(claimValue);
        return this;
    }


    @Override
    public List<Field> getFields() {
        return ImmutableList.of(claim, claimValue);
    }
}
