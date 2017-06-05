package org.codice.ddf.admin.beta;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.beta.discover.PerformCqlQuery;
import org.codice.ddf.admin.beta.discover.PerformQuery;
import org.codice.ddf.admin.beta.persist.CreateMetacard;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;

import com.google.common.collect.ImmutableList;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.MetacardType;
import ddf.catalog.filter.FilterBuilder;

public class CatalogFieldProvider extends BaseFieldProvider {

    public static final String NAME = "catalog";

    public static final String TYPE_NAME = "Catalog";

    public static final String DESCRIPTION =
            "Dat catalog doe.";

    private List<MetacardType> metacardTypes;
    private PerformQuery performQuery;
    private PerformCqlQuery performCqlQuery;

    private CatalogFramework framework;

    public CatalogFieldProvider(CatalogFramework framework, FilterBuilder filterBuilder, List<MetacardType> metacardTypes) {
        super(NAME, TYPE_NAME, DESCRIPTION);
        performQuery = new PerformQuery(framework, filterBuilder, metacardTypes);
        performCqlQuery = new PerformCqlQuery(framework, filterBuilder, metacardTypes);
        this.metacardTypes = metacardTypes;
        this.framework = framework;
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(performQuery, performCqlQuery);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(new CreateMetacard(metacardTypes, framework));
    }
}
