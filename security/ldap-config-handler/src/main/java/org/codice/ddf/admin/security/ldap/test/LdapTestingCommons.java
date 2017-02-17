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
 */

package org.codice.ddf.admin.security.ldap.test;

import static org.codice.ddf.admin.api.validation.LdapValidationUtils.DIGEST_MD5_SASL;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.LDAPS;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.SIMPLE;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.START_TLS;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONFIGURE;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONNECT;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_CONNECTION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.security.ldap.LdapConnectionResult;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.LDAPConnectionFactory;
import org.forgerock.opendj.ldap.LDAPOptions;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.requests.BindRequest;
import org.forgerock.opendj.ldap.requests.DigestMD5SASLBindRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapTestingCommons {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestingCommons.class);

    public LdapConnectionAttempt getLdapConnection(LdapConfiguration ldapConfiguration) {
        LDAPOptions ldapOptions = new LDAPOptions();

        try {
            if (ldapConfiguration.encryptionMethod()
                    .equalsIgnoreCase(LDAPS)) {
                ldapOptions.setSSLContext(SSLContext.getDefault());
            } else if (ldapConfiguration.encryptionMethod()
                    .equalsIgnoreCase(START_TLS)) {
                ldapOptions.setUseStartTLS(true);
            }

            ldapOptions.addEnabledCipherSuite(System.getProperty("https.cipherSuites")
                    .split(","));
            ldapOptions.addEnabledProtocol(System.getProperty("https.protocols")
                    .split(","));

            //sets the classloader so it can find the grizzly protocol handler class
            ldapOptions.setProviderClassLoader(LdapTestingCommons.class.getClassLoader());

        } catch (Exception e) {
            LOGGER.debug("Error prepping LDAP connection", e);
            return new LdapConnectionAttempt(CANNOT_CONFIGURE);
        }

        Connection ldapConnection;

        try {
            ldapConnection = new LDAPConnectionFactory(ldapConfiguration.hostName(),
                    ldapConfiguration.port(),
                    ldapOptions).getConnection();
        } catch (Exception e) {
            LOGGER.debug("Error opening LDAP connection to [{}:{}]",
                    ldapConfiguration.hostName(),
                    ldapConfiguration.port());
            return new LdapConnectionAttempt(CANNOT_CONNECT);
        }

        return new LdapConnectionAttempt(SUCCESSFUL_CONNECTION, ldapConnection);
    }

    public LdapConnectionAttempt bindUserToLdapConnection(LdapConfiguration ldapConfiguration) {
        LdapConnectionAttempt ldapConnectionResult = getLdapConnection(ldapConfiguration);
        if (ldapConnectionResult.result() != SUCCESSFUL_CONNECTION) {
            return ldapConnectionResult;
        }

        Connection connection = ldapConnectionResult.connection();

        try {
            BindRequest bindRequest = selectBindMethod(ldapConfiguration.bindUserMethod(),
                    ldapConfiguration.bindUser(),
                    ldapConfiguration.bindUserPassword(),
                    ldapConfiguration.bindRealm(),
                    null);
            connection.bind(bindRequest);
        } catch (Exception e) {
            LOGGER.debug("Error binding to LDAP", e);
            return new LdapConnectionAttempt(CANNOT_BIND);
        }

        return new LdapConnectionAttempt(SUCCESSFUL_BIND, connection);
    }

    /**
     * Executes a query against the ldap connection
     *
     * @param ldapConnection   Ldap connection to run query on
     * @param ldapSearchBaseDN Base DN to run the query on
     * @param ldapQuery        Query to perform
     * @param searchScope      Scope of query
     * @param maxResults       Max number of results to return from query. Use -1 for all results
     * @param attributes       Optional list of attributes for return projection; if null,
     *                         then all attributes will be returned
     * @return list of results
     */
    public List<SearchResultEntry> getLdapQueryResults(Connection ldapConnection,
            String ldapSearchBaseDN, String ldapQuery, SearchScope searchScope, int maxResults,
            String... attributes) {
        ConnectionEntryReader reader;
        if (attributes == null) {
            reader = ldapConnection.search(ldapSearchBaseDN, searchScope, ldapQuery);
        } else {
            reader = ldapConnection.search(ldapSearchBaseDN, searchScope, ldapQuery, attributes);
        }

        List<SearchResultEntry> entries = new ArrayList<>();
        try {
            while (entries.size() < maxResults && reader.hasNext()) {
                if (!reader.isReference()) {
                    SearchResultEntry resultEntry = reader.readEntry();
                    entries.add(resultEntry);
                } else {
                    reader.readReference();
                }
            }
        } catch (IOException e) {
            reader.close();
        }

        reader.close();
        return entries;
    }

    private static BindRequest selectBindMethod(String bindMethod, String bindUser,
            String bindUserCredentials, String realm, String kdcAddress) {
        BindRequest request;

        // TODO RAP 31 Jan 17: These case statements should operate in a case-insensitive manner
        switch (bindMethod) {
        case SIMPLE:
            request = Requests.newSimpleBindRequest(bindUser, bindUserCredentials.toCharArray());
            break;
        //        case SASL:
        //            request = Requests.newPlainSASLBindRequest(bindUserDN,
        //                    bindUserCredentials.toCharArray());
        //            break;
        //        case GSSAPI_SASL:
        //            request = Requests.newGSSAPISASLBindRequest(bindUserDN,
        //                    bindUserCredentials.toCharArray());
        //            ((GSSAPISASLBindRequest) request).setRealm(realm);
        //            ((GSSAPISASLBindRequest) request).setKDCAddress(kdcAddress);
        //            break;
        case DIGEST_MD5_SASL:
            request = Requests.newDigestMD5SASLBindRequest(bindUser,
                    bindUserCredentials.toCharArray());
            ((DigestMD5SASLBindRequest) request).setCipher(DigestMD5SASLBindRequest.CIPHER_HIGH);
            ((DigestMD5SASLBindRequest) request).getQOPs()
                    .clear();
            ((DigestMD5SASLBindRequest) request).getQOPs()
                    .add(DigestMD5SASLBindRequest.QOP_AUTH_CONF);
            ((DigestMD5SASLBindRequest) request).getQOPs()
                    .add(DigestMD5SASLBindRequest.QOP_AUTH_INT);
            ((DigestMD5SASLBindRequest) request).getQOPs()
                    .add(DigestMD5SASLBindRequest.QOP_AUTH);
            if (realm != null && !realm.equals("")) {
                ((DigestMD5SASLBindRequest) request).setRealm(realm);
            }
            break;
        default:
            request = Requests.newSimpleBindRequest(bindUser, bindUserCredentials.toCharArray());
            break;
        }

        return request;
    }

    public static class LdapConnectionAttempt {

        private LdapConnectionResult result;

        private Connection connection;

        public LdapConnectionAttempt(LdapConnectionResult result) {
            this.result = result;
        }

        public LdapConnectionAttempt(LdapConnectionResult result, Connection value) {
            this.result = result;
            this.connection = value;
        }

        public Connection connection() {
            return connection;
        }

        public LdapConnectionResult result() {
            return result;
        }
    }
}
