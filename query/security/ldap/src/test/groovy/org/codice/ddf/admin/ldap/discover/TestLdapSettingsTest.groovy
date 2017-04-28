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
package org.codice.ddf.admin.ldap.discover

import org.codice.ddf.admin.ldap.LdapTestingCommons
import org.codice.ddf.admin.ldap.TestLdapServer
import spock.lang.Ignore
import spock.lang.Specification

class TestLdapSettingsTest extends Specification {
    static TestLdapServer server
    Map<String, Object> args
    LdapTestSettings action

    def setupSpec() {
        server = TestLdapServer.getInstance().useSimpleAuth()
        server.startListening()
    }

    def cleanupSpec() {
        server.shutdown()
        server = null
    }

    def setup() {
        LdapTestingCommons.loadLdapTestProperties()
        action = new LdapTestSettings()
    }

    @Ignore
    def 'Fail on missing required fields'() {
    }

    @Ignore
    def 'Fail on missing required fields for LDAP Attribute Store'() {
    }

    @Ignore
    def 'Fail to connect to LDAP'() {
    }

    @Ignore
    def 'Fail to bind to LDAP'() {
    }

    @Ignore
    def 'Fail when baseUserDN & baseGroupDN dont exist'() {
    }

    @Ignore
    def 'Fail to find entries in baseUserDN'() {
    }

    @Ignore
    def 'Fail to find usernameAttribute on users in baseUserDN'() {
    }

    @Ignore
    def 'Fail to find entries in baseGroupDn'() {
    }

    @Ignore
    def 'Fail to find specified groupObjectClass in groups'() {
    }
}
