package org.codice.ddf.admin.beta.discover;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.opengis.filter.Filter;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Result;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;

public class PerformQuery extends BaseFunctionField<BooleanField> {
    public static final String NAME = "query";

    public static final String DESCRIPTION = "Executes a query!";

    private CatalogFramework framework;
    private FilterBuilder filterBuilder;

    public PerformQuery(CatalogFramework framework, FilterBuilder filterBuilder) {
        super(NAME, DESCRIPTION, BooleanField.class);
        this.framework = framework;
        this.filterBuilder = filterBuilder;
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public BooleanField performFunction() {

        Filter testFilter = filterBuilder.attribute("testAttribute")
                .is()
                .like()
                .text("testAttributeValue");
         try {
             QueryResponse response = framework.query(new QueryRequestImpl(new QueryImpl(testFilter)));
             List<Result> results = response.getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BooleanField(true);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new PerformQuery(framework, filterBuilder);
    }
}