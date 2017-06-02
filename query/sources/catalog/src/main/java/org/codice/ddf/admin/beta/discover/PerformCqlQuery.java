package org.codice.ddf.admin.beta.discover;

import static org.codice.ddf.admin.beta.discover.TransformCommons.executeQuery;
import static org.codice.ddf.admin.beta.discover.TransformCommons.invalidaSyntaxError;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.beta.fields.CqlQueryField;
import org.codice.ddf.admin.beta.types.MetacardField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.filter.FilterBuilder;

public class PerformCqlQuery extends BaseFunctionField<ListField<MetacardField>> {

    public static final String NAME = "cqlQuery";

    public static final String DESCRIPTION = "Executes a CQL query against the Catalog. Example query: (title like 'sampleTitle') or (title like 'sampleTitle2')";

    CqlQueryField cqlQuery;

    private CatalogFramework framework;

    private FilterBuilder filterBuilder;
    private List<MetacardType> metacardTypes;

    public PerformCqlQuery(CatalogFramework framework, FilterBuilder filterBuilder,
            List<MetacardType> metacardTypes) {
        super(NAME, DESCRIPTION);
        this.framework = framework;
        this.filterBuilder = filterBuilder;
        this.metacardTypes = metacardTypes;
        setReturnType(new ListFieldImpl<>(new MetacardField(metacardTypes)));

        cqlQuery = new CqlQueryField();
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(cqlQuery);
    }

    @Override
    public ListField<MetacardField>  performFunction() {
        Filter filter = null;

        if (Strings.isNullOrEmpty(cqlQuery.getValue())) {
            filter = filterBuilder.attribute(Metacard.ANY_TEXT)
                    .is()
                    .like()
                    .text("*");
        } else {
            try {
                filter = ECQL.toFilter(cqlQuery.getValue());
            } catch (CQLException e) {
                addArgumentMessage(invalidaSyntaxError(cqlQuery.path()));
            }
        }

        return executeQuery(filter, framework, metacardTypes);
    }

    @Override
    public FunctionField<ListField<MetacardField>> newInstance() {
        return new PerformCqlQuery(framework, filterBuilder, metacardTypes);
    }
}
