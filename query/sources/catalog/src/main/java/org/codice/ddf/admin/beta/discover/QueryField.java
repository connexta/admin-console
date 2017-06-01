package org.codice.ddf.admin.beta.discover;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.beta.OperandProvider;
import org.codice.ddf.admin.beta.fields.BooleanAttributeOperands;
import org.codice.ddf.admin.beta.fields.IntegerAttributeOperands;
import org.codice.ddf.admin.beta.fields.StringAttributeOperands;
import org.codice.ddf.admin.beta.types.MetacardAttributeField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;

import com.google.common.collect.ImmutableList;

import ddf.catalog.data.MetacardType;

public class QueryField extends BaseObjectField implements OperandProvider {

    public static final String DEFAULT_FIELD_NAME = "query";
    public static final String DESCRIPTION = "Defines a query to be executed against the catalog framework.";

    private StringAttributeOperands stringOps;
    private IntegerAttributeOperands intOps;
    private BooleanAttributeOperands booleanOps;
    private MetacardAttributeField metacardAttributes;

    protected QueryField(List<MetacardType> metacardTypes) {
        super(DEFAULT_FIELD_NAME, "CatalogQuery", DESCRIPTION);
        stringOps = new StringAttributeOperands();
        intOps = new IntegerAttributeOperands();
        booleanOps = new BooleanAttributeOperands();
        metacardAttributes = new MetacardAttributeField(metacardTypes);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(stringOps, booleanOps, intOps, metacardAttributes);
    }

    public List<FieldOperand> getFieldOperands() {
        return Stream.concat(Stream.concat(
                stringOps.getFieldOperands().stream(),
                booleanOps.getFieldOperands().stream()),
                intOps.getFieldOperands().stream())
                .collect(Collectors.toList());
    }

    //    STRING,
    //
    //    BOOLEAN,
    //
    //    DATE,
    //
    //    SHORT,
    //
    //    INTEGER,
    //
    //    LONG,
    //
    //    FLOAT,
    //
    //    DOUBLE,
    //
    //    GEOMETRY,
    //
    //    BINARY,
    //
    //    XML,
    //
    //    OBJECT
}
