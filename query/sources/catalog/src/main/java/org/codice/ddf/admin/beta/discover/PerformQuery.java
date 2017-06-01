package org.codice.ddf.admin.beta.discover;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.beta.types.MetacardField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.opengis.filter.Filter;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Result;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;

import ddf.catalog.data.MetacardType;

public class PerformQuery extends BaseFunctionField<ListField<MetacardField>> {

    public static final String NAME = "query";

    public static final String DESCRIPTION = "Executes a query against the Catalog.";

    private CatalogFramework framework;
    private FilterBuilder filterBuilder;

    private List<MetacardType> metacardTypes;

    public PerformQuery(CatalogFramework framework, FilterBuilder filterBuilder, List<MetacardType> metacardTypes) {
        super(NAME, DESCRIPTION);
        this.framework = framework;
        this.filterBuilder = filterBuilder;
        this.metacardTypes = metacardTypes;
        setReturnType(new ListFieldImpl<>(new MetacardField(metacardTypes)));
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public ListField<MetacardField> performFunction() {
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
        return new ListFieldImpl<>(MetacardField.class);
    }

    @Override
    public FunctionField<ListField<MetacardField>> newInstance() {
        return new PerformQuery(framework, filterBuilder, metacardTypes);
    }
}