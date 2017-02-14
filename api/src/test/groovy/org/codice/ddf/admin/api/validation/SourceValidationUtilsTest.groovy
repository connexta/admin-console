package org.codice.ddf.admin.api.validation

import org.codice.ddf.admin.api.configurator.Configurator
import org.codice.ddf.admin.api.handler.ConfigurationMessage
import spock.lang.Specification

class SourceValidationUtilsTest extends Specification {

    static final CONFIG_FIELD_ID = "testFieldId"

    static final EXISTING_SOURCE_NAME = "existingSourceName"

    static final NEW_SOURCE_NAME = "newSourceName"

    def configurator

    def sourceValidationUtils

    def setup() {
        configurator = mockConfigurator(EXISTING_SOURCE_NAME)
        sourceValidationUtils = new SourceValidationUtils(configurator)
    }

    def 'test validateNonDuplicateSourceName(String, String) with no existing config with source name'() {
        when:
        List<ConfigurationMessage> results = sourceValidationUtils.validateNonDuplicateSourceName(NEW_SOURCE_NAME, CONFIG_FIELD_ID)

        then:
        results.isEmpty()
    }

    def 'test validateNonDuplicateSourceName(String, String) with duplicate source name'() {
        when:
        List<ConfigurationMessage> results = sourceValidationUtils.validateNonDuplicateSourceName(EXISTING_SOURCE_NAME, CONFIG_FIELD_ID)

        then:
        results.size() == 1
        results.get(0).type() == ConfigurationMessage.MessageType.FAILURE
        results.get(0).configFieldId() == CONFIG_FIELD_ID
        results.get(0).subtype() == ConfigurationMessage.INVALID_FIELD
    }

    def 'test validateNonDuplicateSourceName(String, String) with invalid existing config id property'() {
        when:
        configurator = mockConfigurator(true)
        sourceValidationUtils = new SourceValidationUtils(configurator)
        List<ConfigurationMessage> results = sourceValidationUtils.validateNonDuplicateSourceName(NEW_SOURCE_NAME, CONFIG_FIELD_ID)

        then:
        results.size() == 1
        results.get(0).type() == ConfigurationMessage.MessageType.FAILURE
        results.get(0).configFieldId() == CONFIG_FIELD_ID
        results.get(0).subtype() == ConfigurationMessage.INVALID_FIELD
    }

    def 'test validateNonDuplicateSourceName(String, String) with null or empty sourceName'() {
        when:
        List<ConfigurationMessage> results = sourceValidationUtils.validateNonDuplicateSourceName(sourceName, CONFIG_FIELD_ID)

        then:
        results.size() == 1
        results.get(0).type() == ConfigurationMessage.MessageType.FAILURE
        results.get(0).configFieldId() == CONFIG_FIELD_ID
        results.get(0).subtype() == ConfigurationMessage.MISSING_REQUIRED_FIELD

        where:
        sourceName << [null, ""]
    }

    def mockConfigurator(Object sourceName) {
        return Mock(Configurator) {
            getManagedServiceConfigs(_ as String) >> ["fid": ["id": sourceName]] >> [:]
        }
    }
}