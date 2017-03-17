package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class RealmList extends BaseListField<Realm> {

    public static final String DEFAULT_FIELD_NAME = "realms";
    public static final String DESCRIPTION = "A list of Realms";

    public RealmList() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new Realm());
    }

    @Override
    public RealmList add(Realm value) {
        super.add(value);
        return this;
    }
}