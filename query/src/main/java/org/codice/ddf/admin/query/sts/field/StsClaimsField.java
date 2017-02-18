package org.codice.ddf.admin.query.sts.field;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

import com.google.common.collect.ImmutableList;

public class StsClaimsField extends ListField {

    public static final String DESCRIPTION = "All currently configured claims supported by the STS";

    private List<String> claims = ImmutableList.of("test");

    public StsClaimsField() {
        super("claims");
    }

    public List<String> getClaims() {
        return claims;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public Field getListValueField() {
        return new StsClaimField();
    }
}
