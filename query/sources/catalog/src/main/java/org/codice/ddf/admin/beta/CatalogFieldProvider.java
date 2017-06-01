package org.codice.ddf.admin.beta;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.beta.discover.PerformQuery;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;

import com.google.common.collect.ImmutableList;

public class CatalogFieldProvider extends BaseFieldProvider {

    public static final String NAME = "catalog";

    public static final String TYPE_NAME = "Catalog";

    public static final String DESCRIPTION =
            "Dat catalog doe.";

    private PerformQuery performQuery;

    public CatalogFieldProvider() {
        super(NAME, TYPE_NAME, DESCRIPTION);
        performQuery = new PerformQuery();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(performQuery);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return new ArrayList<>();
    }
}
