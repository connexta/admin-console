package org.codice.ddf.admin.query.api.fields;

import java.util.List;

public interface EnumField extends Field {

    List<ScalarField> getEnumValues();
}
