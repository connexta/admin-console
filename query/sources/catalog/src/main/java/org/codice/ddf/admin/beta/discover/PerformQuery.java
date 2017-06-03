package org.codice.ddf.admin.beta.discover;

import static org.codice.ddf.admin.beta.discover.TransformCommons.executeQuery;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.beta.fields.expression.QueryField;
import org.codice.ddf.admin.beta.types.MetacardField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;

import com.google.common.collect.ImmutableList;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.MetacardType;
import ddf.catalog.filter.FilterBuilder;

public class PerformQuery extends BaseFunctionField<ListField<MetacardField>> {

    public static final String NAME = "query";

    public static final String DESCRIPTION = "Executes a query against the Catalog.";

    private QueryField query;

    private CatalogFramework framework;

    private FilterBuilder filterBuilder;
    private List<MetacardType> metacardTypes;

    public PerformQuery(CatalogFramework framework, FilterBuilder filterBuilder,
            List<MetacardType> metacardTypes) {
        super(NAME, DESCRIPTION);
        query = new QueryField(metacardTypes);

        this.framework = framework;
        this.filterBuilder = filterBuilder;
        this.metacardTypes = metacardTypes;
        setReturnType(new ListFieldImpl<>(new MetacardField(metacardTypes)));
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(query);
    }

    @Override
    public ListField<MetacardField>  performFunction() {
        return executeQuery(query.buildFilter(null, null, filterBuilder), framework, metacardTypes);

    }

    @Override
    public FunctionField<ListField<MetacardField>> newInstance() {
        return new PerformQuery(framework, filterBuilder, metacardTypes);
    }
}