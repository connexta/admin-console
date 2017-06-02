package org.codice.ddf.admin.beta.fields;

import java.util.List;

import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class CqlQueryField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "cqlQuery";
    public static final String FIELD_TYPE_NAME = "CqlQuery";

    public static final String DESCRIPTION =
            "Contextual Query Language is a formal language for representing queries to information retrieval systems such as search engines, "
                    + "bibliographic catalogs and museum collection information. "
                    + "For examples and more information visist http://zing.z3950.org/cql/intro.html";

    public CqlQueryField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Message> validate() {
        return super.validate();
    }
}
