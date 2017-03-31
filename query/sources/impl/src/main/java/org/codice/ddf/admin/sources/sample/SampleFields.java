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
package org.codice.ddf.admin.sources.sample;

import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.SourceInfoListField;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.OpensearchSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;

public class SampleFields {

    public static final SourceConfigUnionField SAMPLE_CSW_CONFIG =
            new CswSourceConfigurationField().outputSchema("sampleOutputSchema")
                    .forceSpatialFilter("true")
                    .sourceName("cswSourceName")
                    .credentials("sampleUsername", "samplePassword")
                    .endpointUrl("cswUrl")
                    .id("cswId");

    public static final SourceInfoField SAMPLE_CSW_SOURCE_INFO =
            new SourceInfoField().sourceHandlerName("cswHandler")
                    .isAvaliable(true)
                    .configuration(SAMPLE_CSW_CONFIG);

    public static final SourceConfigUnionField SAMPLE_WFS_CONFIG =
            new WfsSourceConfigurationField().sourceName("wfsSourceName")
                    .credentials("sampleUsername", "samplePassword")
                    .endpointUrl("wfsUrl")
                    .id("wfsId");

    public static final SourceInfoField SAMPLE_WFS_SOURCE_INFO =
            new SourceInfoField().sourceHandlerName("wfsHandler")
                    .isAvaliable(true)
                    .configuration(SAMPLE_WFS_CONFIG);

    public static final SourceConfigUnionField SAMPLE_OPENSEARCH_CONFIG =
            new OpensearchSourceConfigurationField().sourceName("opensearchSourceName")
                    .credentials("sampleUsername", "samplePassword")
                    .endpointUrl("opensearchUrl")
                    .id("opensearchId");

    public static final SourceInfoField SAMPLE_OPENSEARCH_SOURCE_INFO =
            new SourceInfoField().sourceHandlerName("opensearchHandler")
                    .isAvaliable(true)
                    .configuration(SAMPLE_OPENSEARCH_CONFIG);

    public static final SourceInfoListField SAMPLE_SOURCES_INFO_LIST =
            new SourceInfoListField().add(SAMPLE_CSW_SOURCE_INFO)
                    .add(SAMPLE_WFS_SOURCE_INFO)
                    .add(SAMPLE_OPENSEARCH_SOURCE_INFO);
}
