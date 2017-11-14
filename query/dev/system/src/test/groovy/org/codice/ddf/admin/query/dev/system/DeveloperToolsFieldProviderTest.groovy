package org.codice.ddf.admin.query.dev.system

import org.codice.ddf.admin.api.fields.FunctionField
import spock.lang.Specification

class DeveloperToolsFieldProviderTest extends Specification {

    private DeveloperToolsFieldProvider devFieldProvider = new DeveloperToolsFieldProvider(null, null)

    def 'Verify discovery functions are returned successfully'() {
        when:
        List<FunctionField> discoveryFunctions = devFieldProvider.getDiscoveryFunctions()

        then:
        discoveryFunctions.size() == 2
    }

    def 'Verify mutation functions are returned successfully'() {
        when:
        List<FunctionField> persistFunctions = devFieldProvider.getMutationFunctions()

        then:
        persistFunctions.size() == 3
    }
}
