package groovy.org.codice.ddf.admin.security.wcpm.persist

import org.apache.karaf.jaas.config.JaasRealm
import org.codice.ddf.security.handler.api.AuthenticationHandler
import org.codice.ddf.security.handler.api.HandlerResult

import javax.security.auth.login.AppConfigurationEntry
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class WcpmTestingCommons {

    static final BASIC = 'basic'
    static final SAML = 'SAML'
    static final PKI = 'PKI'
    static final KARAF = 'karaf'

    static final AuthenticationHandler BASIC_HANDLER = createAuthHandler(BASIC)
    static final AuthenticationHandler SAML_HANDLER = createAuthHandler(SAML)
    static final AuthenticationHandler PKI_HANDLER = createAuthHandler(PKI)
    static final JaasRealm KARAF_REALM = createRealm(KARAF)

    static AuthenticationHandler createAuthHandler(String authType) {
        new AuthenticationHandler() {
            @Override
            String getAuthenticationType() {
                return authType
            }

            @Override
            HandlerResult getNormalizedToken(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain, boolean b) throws ServletException {
                return null
            }

            @Override
            HandlerResult handleError(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException {
                return null
            }
        }
    }

    static JaasRealm createRealm(String realm) {
        new JaasRealm() {
            @Override
            String getName() {
                return realm
            }

            @Override
            int getRank() {
                return 0
            }

            @Override
            AppConfigurationEntry[] getEntries() {
                return new AppConfigurationEntry[0]
            }
        }
    }
}
