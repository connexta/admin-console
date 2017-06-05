package org.codice.ddf.admin.beta.persist;

import java.io.Serializable;
import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.beta.types.MetacardField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;

import com.google.common.collect.ImmutableList;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.operation.impl.CreateRequestImpl;
import ddf.catalog.source.IngestException;
import ddf.catalog.source.SourceUnavailableException;

public class CreateMetacard extends BaseFunctionField<MetacardField> {

    public static final String FIELD_NAME = "createMetacard";

    public static final String FIELD_DESCRIPTION = "Create a new catalog metacard.";

    private MetacardField metacard;

    private List<MetacardType> metacardTypes;

    private CatalogFramework framework;

    public CreateMetacard(List<MetacardType> metacardTypes, CatalogFramework framework) {
        super(FIELD_NAME, FIELD_DESCRIPTION);
        this.metacardTypes = metacardTypes;
        this.framework = framework;
        metacard = new MetacardField(metacardTypes);
        setReturnType(metacard);
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(metacard);
    }

    @Override
    public FunctionField<MetacardField> newInstance() {
        return new CreateMetacard(metacardTypes, framework);
    }

    @Override
    public MetacardField performFunction() {
        MetacardImpl metacardImpl = new MetacardImpl();

        metacard.getFields()
                .stream()
                .forEach(field -> {
                    metacardImpl.setAttribute(field.fieldName(), (Serializable) field.getValue());
                });

        try {
            // TODO: return created metacards as result
            framework.create(new CreateRequestImpl(
                    metacardImpl))
                    .getCreatedMetacards();
        } catch (IngestException e) {
        } catch (SourceUnavailableException e) {
        }

        return metacard;
    }
}
