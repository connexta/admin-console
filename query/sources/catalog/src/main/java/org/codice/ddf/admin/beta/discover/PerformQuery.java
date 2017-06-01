package org.codice.ddf.admin.beta.discover;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;

public class PerformQuery extends BaseFunctionField<BooleanField> {
    public static final String NAME = "query";

    public static final String DESCRIPTION = "Executes a query!";


    public PerformQuery() {
        super(NAME, DESCRIPTION, BooleanField.class);
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public BooleanField performFunction() {
        return new BooleanField(true);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new PerformQuery();
    }
}