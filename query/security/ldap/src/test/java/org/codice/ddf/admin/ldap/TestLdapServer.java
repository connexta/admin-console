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
package org.codice.ddf.admin.ldap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.InMemoryRequestHandler;
import com.unboundid.ldap.listener.InMemorySASLBindHandler;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustStoreTrustManager;

public class TestLdapServer {

    private InMemoryDirectoryServer realServer;

    private InMemoryDirectoryServerConfig serverConfig;

    public static String getBaseDistinguishedName() {
        return "dc=example,dc=com";
    }

    public static TestLdapServer getInstance() {
        TestLdapServer object = new TestLdapServer();
        try {
            InMemoryListenerConfig ldapConfig = InMemoryListenerConfig.createLDAPConfig(
                    getBaseDistinguishedName(),
                    getLdapPort());
            InMemoryListenerConfig ldapsConfig = InMemoryListenerConfig.createLDAPSConfig(
                    "ldaps",
                    getLdapSecurePort(),
                    object.getServerSSLContext()
                            .getServerSocketFactory());
            object.serverConfig = new InMemoryDirectoryServerConfig(getBaseDistinguishedName());
            object.serverConfig.setListenerConfigs(ldapConfig, ldapsConfig);

        } catch (LDAPException e) {
            fail(e.getMessage());
        }
        return object;
    }

    public static String getBasicAuthPassword() {
        return "secret";
    }

    public static String getBasicAuthDn() {
        return "cn=admin";
    }


    public static int getLdapPort() {
        // return server.getListenPort("ldap");
        return 1389;

    }

    public static int getLdapSecurePort() {
        // return server.getListenPort("ldaps");
        return 1636;
    }

    public static String getHostname() {
        return "localhost";
    }

    public static String getUrl(String protocol) {
        String url = null;
        switch (protocol) {
        case "ldap":
            url = String.format("ldap://%s:%s", getHostname(), getLdapPort());
            break;
        case "ldaps":
            url = String.format("ldaps://%s:%s", getHostname(), getLdapSecurePort());
            break;
        default:
            fail("Unknown LDAP bind protocol");
        }
        return url;
    }

    SSLContext getServerSSLContext() {
        try {
            char[] keyStorePassword = "changeit".toCharArray();
            String keystore = getClass().getResource("/serverKeystore.jks")
                    .getFile();
            KeyStoreKeyManager keyManager = new KeyStoreKeyManager(keystore,
                    keyStorePassword,
                    "JKS",
                    getHostname());
            String truststore = getClass().getResource("/serverTruststore.jks")
                    .getFile();
            TrustStoreTrustManager trustManager = new TrustStoreTrustManager(truststore,
                    keyStorePassword,
                    null,
                    false);
            return new SSLUtil(keyManager, trustManager).createSSLContext();
        } catch (GeneralSecurityException e) {
            fail(e.getMessage());
        }
        return null;
    }

    public TestLdapServer useSimpleAuth() {

        try {
            serverConfig.addAdditionalBindCredentials(getBasicAuthDn(), getBasicAuthPassword());
        } catch (LDAPException e) {
            fail(e.getMessage());
        }
        return this;
    }

    public TestLdapServer startListening() {
        try {
            realServer = new InMemoryDirectoryServer(serverConfig);
            realServer.startListening();
        } catch (LDAPException e) {
            fail(e.getMessage());
        }
        loadLdifFile();
        return this;
    }

    public void shutdown() {
        if (realServer != null) {
            realServer.shutDown(true);
        }
        realServer = null;
    }

    public Set<Principal> emptyPrincipals() {
        return new HashSet<>();
    }

    void loadLdifFile() {
        try (InputStream ldifStream = getClass().getResourceAsStream("/test-ldap.ldif")) {
            assertThat("Cannot find LDIF test resource file", ldifStream, is(notNullValue()));
            LDIFReader reader = new LDIFReader(ldifStream);
            LDIFChangeRecord readEntry;
            while ((readEntry = reader.readChangeRecord()) != null) {
                readEntry.processChange(realServer);
            }
        } catch (IOException | LDIFException | LDAPException e) {
            fail(e.getMessage());
        }
    }

    public class Testing extends             InMemorySASLBindHandler {

        @Override
        public String getSASLMechanismName() {
            return null;
        }

        @Override
        public BindResult processSASLBind(InMemoryRequestHandler handler, int messageID, DN bindDN,
                ASN1OctetString credentials, List<Control> controls) {
            return null;
        }
    }
}
