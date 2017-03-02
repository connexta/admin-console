package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class RealmList extends BaseListField<StringField> {

    public static final String DEFAULT_FIELD_NAME = "realms";
    public static final String DESCRIPTION = "A list of Realms";

    public RealmList() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public Realm getListValueField() {
        return new Realm();
    }

    @Override
    public RealmList addField(StringField value) {
        super.addField(value);
        return this;
    }
}