package org.codice.ddf.admin.api.validation

import org.codice.ddf.admin.api.config.sources.SourceConfiguration
import org.codice.ddf.admin.api.configurator.Configurator
import org.codice.ddf.admin.api.handler.ConfigurationMessage
import spock.lang.Specification

class SourceValidationUtilsTest extends Specification {

    static final CONFIG_FIELD_ID = SourceConfiguration.SOURCE_NAME

    static final EXISTING_SOURCE_NAME = "existingSourceName"

    static final NEW_SOURCE_NAME = "newSourceName"

    static final FACTORY_IDS = Collections.singletonList("fid")

    def configurator

    def sourceValidationUtils

    def setup() {
        configurator = mockConfigurator(EXISTING_SOURCE_NAME)
        sourceValidationUtils = new SourceValidationUtils()
    }

    def 'test validateSourceName(String, String) with no existing config with source name'() {
        when:
        List<ConfigurationMessage> results = sourceValidationUtils.validateSourceName(NEW_SOURCE_NAME, FACTORY_IDS, configurator)

        then:
        results.isEmpty()
    }

    def 'test validateSourceName(String, String) with duplicate source name'() {
        when:
        List<ConfigurationMessage> results = sourceValidationUtils.validateSourceName(EXISTING_SOURCE_NAME, FACTORY_IDS, configurator)

        then:
        results.size() == 1
        results.get(0).type() == ConfigurationMessage.MessageType.FAILURE
        results.get(0).configFieldId() == CONFIG_FIELD_ID
        results.get(0).subtype() == ConfigurationMessage.INVALID_FIELD
    }

    def 'test validateSourceName(String, String) with invalid existing config id property'() {
        when:
        configurator = mockConfigurator(true)
        sourceValidationUtils = new SourceValidationUtils()
        List<ConfigurationMessage> results = sourceValidationUtils.validateSourceName(NEW_SOURCE_NAME, FACTORY_IDS, configurator)

        then:
        results.size() == 1
        results.get(0).type() == ConfigurationMessage.MessageType.FAILURE
        results.get(0).subtype() == ConfigurationMessage.INTERNAL_ERROR
    }

    def mockConfigurator(Object sourceName) {
        return Mock(Configurator) {
            getManagedServiceConfigs(_ as String) >> ["fid": ["id": sourceName]] >> [:]
        }
    }
}