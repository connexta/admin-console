package groovy.org.codice.ddf.admin.security.wcpm.persist

import org.codice.ddf.admin.api.poller.EnumValuePoller
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.security.common.fields.wcpm.Realm
import org.codice.ddf.admin.security.wcpm.RealmTypesPoller
import org.codice.ddf.admin.security.wcpm.discover.GetRealms
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader
import spock.lang.Specification

import static groovy.org.codice.ddf.admin.security.wcpm.persist.WcpmTestingCommons.KARAF
import static groovy.org.codice.ddf.admin.security.wcpm.persist.WcpmTestingCommons.KARAF_REALM

class GetRealmsSpec extends Specification {

    ServiceReader serviceReader
    RealmTypesPoller realmTypesPoller
    GetRealms getRealms

    def setup() {
        realmTypesPoller = new RealmTypesPoller()
        realmTypesPoller.setRealms([KARAF_REALM])
        serviceReader = Mock(ServiceReader)
        serviceReader.getServices(EnumValuePoller.class, Realm.REALM_POLLER_FILTER) >> ([realmTypesPoller] as Set)
        getRealms = new GetRealms(serviceReader)
    }

    def 'Successfully retrieves realms from realm value poller'() {
        when:
        Report report = getRealms.getValue()

        then:
        report.getErrorMessages().isEmpty()
        report.getResult().getValue() == [KARAF]
    }
}
