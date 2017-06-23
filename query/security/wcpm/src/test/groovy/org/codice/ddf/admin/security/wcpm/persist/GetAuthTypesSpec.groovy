package groovy.org.codice.ddf.admin.security.wcpm.persist

import org.codice.ddf.admin.api.poller.EnumValuePoller
import org.codice.ddf.admin.security.wcpm.discover.GetAuthTypes
import spock.lang.Specification

import static groovy.org.codice.ddf.admin.security.wcpm.persist.WcpmTestingCommons.*
import org.codice.ddf.admin.api.report.ReportWithResult
import org.codice.ddf.admin.security.common.fields.wcpm.AuthType
import org.codice.ddf.admin.security.wcpm.AuthTypesPoller
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader

class GetAuthTypesSpec extends Specification {

    ServiceReader serviceReader
    AuthTypesPoller authTypesPoller
    GetAuthTypes getAuthTypes

    def setup() {
        authTypesPoller = new AuthTypesPoller()
        authTypesPoller.setAuthHandlers([BASIC_HANDLER, SAML_HANDLER, PKI_HANDLER])
        serviceReader = Mock(ServiceReader)
        serviceReader.getServices(EnumValuePoller.class, AuthType.AUTH_TYPE_POLLER_FILTER) >> ([authTypesPoller] as Set)
        getAuthTypes = new GetAuthTypes(serviceReader)
    }

    def 'Successfully retrieve auth type definitions from the auth type poller'() {
        when:
        ReportWithResult report = getAuthTypes.getValue()

        then:
        report.messages().isEmpty()
        report.result().getValue() == [BASIC, SAML, PKI]
    }
}
