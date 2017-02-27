package org.codice.ddf.admin.query.api.fields;

import java.util.List;

public interface UnionField extends Field {

    List<ObjectField> getUnionTypes();
}
