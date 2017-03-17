package org.codice.ddf.admin.query.sources.sample;

import org.codice.ddf.admin.query.sources.common.SourceConfigUnionField;
import org.codice.ddf.admin.query.sources.common.SourceInfoField;
import org.codice.ddf.admin.query.sources.common.SourceInfoListField;
import org.codice.ddf.admin.query.sources.common.fields.CswSourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.fields.OpensearchSourceConfigurationField;
import org.codice.ddf.admin.query.sources.common.fields.WfsSourceConfigurationField;

public class SampleFields {

    public static final SourceConfigUnionField SAMPLE_CSW_CONFIG = new CswSourceConfigurationField()
            .outputSchema("sampleOutputSchema")
            .forceSpatialFilter("true")
            .sourceName("cswSourceName")
            .credentials("sampleUsername", "samplePassword")
            .endpointUrl("cswUrl")
            .id("cswId");

    public static final SourceInfoField SAMPLE_CSW_SOURCE_INFO = new SourceInfoField()
            .sourceHandlerName("cswHandler")
            .isAvaliable(true)
            .configuration(SAMPLE_CSW_CONFIG);

    public static final SourceConfigUnionField SAMPLE_WFS_CONFIG = new WfsSourceConfigurationField()
            .sourceName("wfsSourceName")
            .credentials("sampleUsername", "samplePassword")
            .endpointUrl("wfsUrl")
            .id("wfsId");

    public static final SourceInfoField SAMPLE_WFS_SOURCE_INFO = new SourceInfoField()
            .sourceHandlerName("wfsHandler")
            .isAvaliable(true)
            .configuration(SAMPLE_WFS_CONFIG);

    public static final SourceConfigUnionField SAMPLE_OPENSEARCH_CONFIG = new OpensearchSourceConfigurationField()
            .sourceName("opensearchSourceName")
            .credentials("sampleUsername", "samplePassword")
            .endpointUrl("opensearchUrl")
            .id("opensearchId");

    public static final SourceInfoField SAMPLE_OPENSEARCH_SOURCE_INFO = new SourceInfoField()
            .sourceHandlerName("opensearchHandler")
            .isAvaliable(true)
            .configuration(SAMPLE_OPENSEARCH_CONFIG);

    public static final SourceInfoListField SAMPLE_SOURCES_INFO_LIST = new SourceInfoListField().add(SAMPLE_CSW_SOURCE_INFO)
            .add(SAMPLE_WFS_SOURCE_INFO)
            .add(SAMPLE_OPENSEARCH_SOURCE_INFO);
}
