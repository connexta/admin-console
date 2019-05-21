package groovy.org.codice.ddf.admin.security.wcpm.persist


import org.codice.ddf.platform.filter.AuthenticationException
import org.codice.ddf.platform.filter.FilterChain
import org.codice.ddf.security.handler.api.AuthenticationHandler
import org.codice.ddf.security.handler.api.HandlerResult

import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class WcpmTestingCommons {

    static final BASIC = 'basic'
    static final SAML = 'SAML'
    static final PKI = 'PKI'

    static final AuthenticationHandler BASIC_HANDLER = createAuthHandler(BASIC)
    static final AuthenticationHandler SAML_HANDLER = createAuthHandler(SAML)
    static final AuthenticationHandler PKI_HANDLER = createAuthHandler(PKI)

    static AuthenticationHandler createAuthHandler(String authType) {
        new AuthenticationHandler() {
            @Override
            String getAuthenticationType() {
                return authType
            }

            @Override
            HandlerResult getNormalizedToken(ServletRequest request, ServletResponse response, FilterChain chain, boolean resolve) throws AuthenticationException {
                return null
            }

            @Override
            HandlerResult handleError(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws AuthenticationException {
                return null
            }
        }
    }
}
