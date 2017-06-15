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
package org.codice.ddf.admin.ldap.commons;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.cannotConnectError;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedTestSetup;
import static org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod.DIGEST_MD5_SASL;
import static org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod.SIMPLE;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.LDAPS;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.START_TLS;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
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

public class LdapTestingUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestingUtils.class);

    private static final String CIPHER_SUITES_SYS_PROP = "https.cipherSuites";

    private static final String HTTP_PROTOCOLS_SYS_PROP = "https.protocols";

    /**
     * Attempts to connect to the given ldap address given the hostname, port, and encryptionMethod
     * <p>
     * Possible message types: CANNOT_CONFIGURE, CANNOT_CONNECT
     *
     * @return connection attempt wrapper object
     */
    public LdapConnectionAttempt getLdapConnection(LdapConnectionField connection) {
        LDAPOptions ldapOptions = new LDAPOptions();

        try {
            if (connection.encryptionMethod()
                    .equals(LDAPS)) {
                ldapOptions.setSSLContext(getSslContext());
            } else if (connection.encryptionMethod()
                    .equals(START_TLS)) {
                ldapOptions.setUseStartTLS(true);
            }

            ldapOptions.addEnabledCipherSuite(System.getProperty(CIPHER_SUITES_SYS_PROP)
                    .split(","));
            ldapOptions.addEnabledProtocol(System.getProperty(HTTP_PROTOCOLS_SYS_PROP)
                    .split(","));

            //sets the classloader so it can find the grizzly protocol handler class
            ldapOptions.setProviderClassLoader(LdapTestingUtils.class.getClassLoader());

        } catch (Exception e) {
            LOGGER.debug("Error prepping LDAP connection", e);
            return new LdapConnectionAttempt().addResultMessage(failedTestSetup());
        }

        Connection ldapConnection;

        try (LDAPConnectionFactory connectionFactory = new LDAPConnectionFactory(connection.hostname(),
                connection.port(),
                ldapOptions)) {
            ldapConnection = connectionFactory.getConnection();
        } catch (Exception e) {
            LOGGER.debug("Error opening LDAP connection to [{}:{}]",
                    connection.hostname(),
                    connection.port());
            return new LdapConnectionAttempt().addArgumentMessage(cannotConnectError(connection.path()));
        }

        return new LdapConnectionAttempt(ldapConnection);
    }

    /**
     * Binds the user to the LDAP connection.
     * <p>
     * Possible message types: CANNOT_CONFIGURE, CANNOT_CONNECT, CANNOT_BIND
     *
     * @param connField
     * @param bindInfo
     * @return
     */
    public LdapConnectionAttempt bindUserToLdapConnection(LdapConnectionField connField,
            LdapBindUserInfo bindInfo) {
        // This ConnectionAttempt intentionally not closed as its internal connection is bound
        // and rewrapped in a new ConnectionAttempt. It is only closed in the case of a bind
        // failure.
        LdapConnectionAttempt connectionAttempt = getLdapConnection(connField);
        if (!connectionAttempt.isResultPresent()) {
            return connectionAttempt;
        }

        Connection connection = connectionAttempt.result();

        try {
            BindRequest bindRequest = selectBindMethod(bindInfo.bindMethod(),
                    bindInfo.credentialsField()
                            .username(),
                    bindInfo.credentialsField()
                            .password(),
                    bindInfo.realm(),
                    null);
            connection.bind(bindRequest);
        } catch (Exception e) {
            LOGGER.debug("Error binding to LDAP", e);
            try {
                connectionAttempt.close();
            } catch (IOException closeException) {
                LOGGER.warn("Error closing LDAP connection", closeException);
            }
            return new LdapConnectionAttempt().addArgumentMessage(LdapMessages.cannotBindError(
                    bindInfo.path()));
        }

        return new LdapConnectionAttempt(connection);
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

    private static BindRequest selectBindMethod(String bindMethod, String bindUser, String password,
            String realm, String kdcAddress) {
        BindRequest request;

        // TODO RAP 31 Jan 17: These case statements should operate in a case-insensitive manner
        switch (bindMethod) {
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
            request = Requests.newDigestMD5SASLBindRequest(bindUser, password.toCharArray());
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
        case SIMPLE:
        default:
            request = Requests.newSimpleBindRequest(bindUser, password.toCharArray());
            break;
        }

        return request;
    }

    public SSLContext getSslContext() throws NoSuchAlgorithmException {
        return SSLContext.getDefault();
    }
}
