/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.sources.fields.type;

import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;

import com.google.common.collect.ImmutableList;

public class OpenSearchSourceConfigurationField extends SourceConfigUnionField {

    private static final String FIELD_TYPE_NAME = "OpenSearchConfiguration";

    private static final String DESCRIPTION = "A specification for querying geospatial data when federating. Query a Metadata Catalog via OpenSearch specifications for query searches.";

    public OpenSearchSourceConfigurationField() {
        super(FIELD_TYPE_NAME, DESCRIPTION);
        factoryPid(OPENSEARCH_FACTORY_PID);
    }

    @Override
    public List<Field> getFields() {
        // TODO: 4/28/17 phuffer -  Add additional fields once implemented
        return new ImmutableList.Builder<Field>().addAll(super.getFields())
                .build();
    }
}
