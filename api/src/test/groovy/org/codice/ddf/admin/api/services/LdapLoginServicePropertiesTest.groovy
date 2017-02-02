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
package org.codice.ddf.admin.api.services

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.validation.LdapValidationUtils
import org.codice.ddf.admin.api.validation.ValidationUtils
import spock.lang.Specification

class LdapLoginServicePropertiesTest extends Specification {

    LdapLoginServiceProperties ldapLoginServiceProperties

    static final TEST_SERVICE_PID = "testServicePidKey"

    static final TEST_FACTORY_PID = "testFactoryPidKey"

    static final TEST_LDAP_BIND_USER = "testLdapBindUser"

    static final TEST_LDAP_BIND_USER_PASS = "testLdapBindUserPass"

    static final TEST_BIND_METHOD = "testBindMethod"

    static final TEST_REALM = "testRealm"

    static final TEST_USER_NAME_ATTRIBUTE = "testUserNameAttribute"

    static final TEST_USER_BASE_DN = "testUserBaseDn"

    static final TEST_GROUP_BASE_DN = "testGroupBaseDn"

    static final TEST_PORT = 389

    static final TEST_HOST_NAME = "ds.example.com"

    static final TEST_HOST_AND_PORT = TEST_HOST_NAME + ":" + TEST_PORT

    static final TEST_LDAP_URL = "ldap://" + "ds.example.com" + ":" + TEST_PORT

    def setup() {
        ldapLoginServiceProperties = new LdapLoginServiceProperties()
    }

    def 'test ldapLoginServiceToLdapConfiguration(Map<String, Objects>) success all fields present'() {
        setup:
        def properties = [ValidationUtils.SERVICE_PID_KEY, TEST_SERVICE_PID,
                          ValidationUtils.FACTORY_PID_KEY, TEST_FACTORY_PID,
                          LdapLoginServiceProperties.LDAP_BIND_USER_DN, TEST_LDAP_BIND_USER,
                          LdapLoginServiceProperties.LDAP_BIND_USER_PASS, TEST_LDAP_BIND_USER_PASS,
                          LdapLoginServiceProperties.BIND_METHOD, TEST_BIND_METHOD,
                          LdapLoginServiceProperties.REALM, TEST_REALM,
                          LdapLoginServiceProperties.USER_NAME_ATTRIBUTE, TEST_USER_NAME_ATTRIBUTE,
                          LdapLoginServiceProperties.USER_BASE_DN, TEST_USER_BASE_DN,
                          LdapLoginServiceProperties.GROUP_BASE_DN, TEST_GROUP_BASE_DN,
                          LdapLoginServiceProperties.LDAP_URL, TEST_LDAP_URL,
                          LdapLoginServiceProperties.START_TLS, true]
                .toSpreadMap()

        when:
        def ldapConfiguration = ldapLoginServiceProperties.ldapLoginServiceToLdapConfiguration(properties)

        then:
        ldapConfiguration.servicePid() == TEST_SERVICE_PID
        ldapConfiguration.factoryPid() == TEST_FACTORY_PID
        ldapConfiguration.bindUser() == TEST_LDAP_BIND_USER
        ldapConfiguration.bindUserPassword() == TEST_LDAP_BIND_USER_PASS
        ldapConfiguration.bindUserMethod() == TEST_BIND_METHOD
        ldapConfiguration.bindRealm() == TEST_REALM
        ldapConfiguration.userNameAttribute() == TEST_USER_NAME_ATTRIBUTE
        ldapConfiguration.baseUserDn() == TEST_USER_BASE_DN
        ldapConfiguration.baseGroupDn() == TEST_GROUP_BASE_DN
        ldapConfiguration.port() == TEST_PORT
        ldapConfiguration.hostName() == TEST_HOST_NAME
        ldapConfiguration.encryptionMethod() == LdapLoginServiceProperties.START_TLS
        ldapConfiguration.ldapUseCase() == LdapValidationUtils.AUTHENTICATION
    }

    def 'test ldapLoginServiceToLdapConfiguration(Map<String, Objects>) ldapUrl does not match Pattern'() {
        setup:
        def properties = [LdapLoginServiceProperties.LDAP_URL, TEST_HOST_AND_PORT,
                          LdapLoginServiceProperties.START_TLS, false]
                .toSpreadMap()

        when:
        def ldapConfiguration = ldapLoginServiceProperties.ldapLoginServiceToLdapConfiguration(properties)

        then:
        ldapConfiguration.port() == 389
        ldapConfiguration.hostName() == TEST_HOST_NAME
        ldapConfiguration.encryptionMethod() == "ldap"

    }

    def 'test ldapLoginServiceToLdapConfiguration(Map<String, Objects>) success with all null fields'() {
        setup:
        def properties = [ValidationUtils.SERVICE_PID_KEY, null,
                          ValidationUtils.FACTORY_PID_KEY, null,
                          LdapLoginServiceProperties.LDAP_BIND_USER_DN, null,
                          LdapLoginServiceProperties.LDAP_BIND_USER_PASS, null,
                          LdapLoginServiceProperties.BIND_METHOD, null,
                          LdapLoginServiceProperties.REALM, null,
                          LdapLoginServiceProperties.USER_NAME_ATTRIBUTE, null,
                          LdapLoginServiceProperties.USER_BASE_DN, null,
                          LdapLoginServiceProperties.GROUP_BASE_DN, null,
                          LdapLoginServiceProperties.LDAP_URL, null,
                          LdapLoginServiceProperties.START_TLS, false]
                .toSpreadMap()

        when:
        def ldapConfiguration = ldapLoginServiceProperties.ldapLoginServiceToLdapConfiguration(properties)

        then:
        ldapConfiguration.servicePid() == null
        ldapConfiguration.factoryPid() == null
        ldapConfiguration.bindUser() == null
        ldapConfiguration.bindUserPassword() == null
        ldapConfiguration.bindUserMethod() == null
        ldapConfiguration.bindRealm() == null
        ldapConfiguration.userNameAttribute() == null
        ldapConfiguration.baseUserDn() == null
        ldapConfiguration.baseGroupDn() == null
        ldapConfiguration.port() == 0
        ldapConfiguration.hostName() == null
        ldapConfiguration.encryptionMethod() == null
        ldapConfiguration.ldapUseCase() == LdapValidationUtils.AUTHENTICATION
    }

    def 'test ldapConfigurationToLdapLoginService(LdapConfiguration) success on all fields'() {
        setup:
        LdapConfiguration ldapConfiguration = Mock(LdapConfiguration) {
            encryptionMethod() >> encryptMethod
            hostName() >> TEST_HOST_NAME
            port() >> 389
            bindUser() >> TEST_LDAP_BIND_USER
            bindUserPassword() >> TEST_LDAP_BIND_USER_PASS
            bindUserMethod() >> TEST_BIND_METHOD
            bindRealm() >> TEST_REALM
            userNameAttribute() >> TEST_USER_NAME_ATTRIBUTE
            baseUserDn() >> TEST_USER_BASE_DN
            baseGroupDn() >> TEST_BIND_METHOD
        }

        when:
        Map result = ldapLoginServiceProperties.ldapConfigurationToLdapLoginService(ldapConfiguration)

        then:
        result.get(LdapLoginServiceProperties.LDAP_URL) == encryptMethod + "://" + TEST_HOST_AND_PORT
        result.get(LdapLoginServiceProperties.START_TLS) == "false"
        result.get(LdapLoginServiceProperties.LDAP_BIND_USER_DN) == TEST_LDAP_BIND_USER
        result.get(LdapLoginServiceProperties.LDAP_BIND_USER_PASS) == TEST_LDAP_BIND_USER_PASS
        result.get(LdapLoginServiceProperties.REALM) == TEST_REALM
        result.get(LdapLoginServiceProperties.USER_NAME_ATTRIBUTE) == TEST_USER_NAME_ATTRIBUTE
        result.get(LdapLoginServiceProperties.USER_BASE_DN) == TEST_USER_BASE_DN
        result.get(LdapLoginServiceProperties.GROUP_BASE_DN) == TEST_BIND_METHOD

        where:
        encryptMethod | _
        "ldaps"       | _
        "ldap"        | _
    }

    def 'test ldapConfigurationToLdapLoginService(LdapConfiguration) startTls'() {
        setup:
        LdapConfiguration ldapConfiguration = Mock(LdapConfiguration) {
            encryptionMethod() >> LdapLoginServiceProperties.START_TLS
            hostName() >> TEST_HOST_NAME
            port() >> 389
            bindUser() >> TEST_LDAP_BIND_USER
            bindUserPassword() >> TEST_LDAP_BIND_USER_PASS
            bindUserMethod() >> TEST_BIND_METHOD
            bindRealm() >> TEST_REALM
            userNameAttribute() >> TEST_USER_NAME_ATTRIBUTE
            baseUserDn() >> TEST_USER_BASE_DN
            baseGroupDn() >> TEST_BIND_METHOD
        }

        when:
        Map result = ldapLoginServiceProperties.ldapConfigurationToLdapLoginService(ldapConfiguration)

        then:
        result.get(LdapLoginServiceProperties.LDAP_URL) == TEST_LDAP_URL
        result.get(LdapLoginServiceProperties.START_TLS) == "true"
        result.get(LdapLoginServiceProperties.LDAP_BIND_USER_DN) == TEST_LDAP_BIND_USER
        result.get(LdapLoginServiceProperties.LDAP_BIND_USER_PASS) == TEST_LDAP_BIND_USER_PASS
        result.get(LdapLoginServiceProperties.REALM) == TEST_REALM
        result.get(LdapLoginServiceProperties.USER_NAME_ATTRIBUTE) == TEST_USER_NAME_ATTRIBUTE
        result.get(LdapLoginServiceProperties.USER_BASE_DN) == TEST_USER_BASE_DN
        result.get(LdapLoginServiceProperties.GROUP_BASE_DN) == TEST_BIND_METHOD
    }

    def 'test ldapConfigToLdapClaimsHandlerService(LdapConfiguration) null LdapConfiguration'() {
        when:
        Map result = ldapLoginServiceProperties.ldapConfigurationToLdapLoginService(null)

        then:
        result == Collections.emptyMap()
    }
}