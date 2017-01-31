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

class LdapClaimsHandlerServicePropertiesTest extends Specification {

    LdapClaimsHandlerServiceProperties ldapClaimsHandlerServiceProperties

    static final TEST_SERVICE_PID = "someServicePid"

    static final TEST_PORT = 389

    static final TEST_HOST_NAME = "ds.example.com"

    static final TEST_URI_SCHEME = "ldap"

    static final TEST_URL = TEST_URI_SCHEME + "://" + TEST_HOST_NAME + ":" + TEST_PORT

    static final TEST_BIND_USER_DN = "someBindUserDn"

    static final TEST_PASSWORD = "password"

    static final TEST_BIND_METHOD = "someBindMethod"

    static final TEST_LOGIN_USER_ATTRIBUTE = "someLoginUserAttribute"

    static final TEST_USER_BASE_DN = "someUserBaseDn"

    static final TEST_GROUP_BASE_DN = "someGroupBaseDn"

    static final TEST_OBJECT_CLASS = "*"

    static final TEST_MEMBERSHIP_USER_ATTRIBUTE = "someMembershipUserAttribute"

    static final TEST_MEMBER_NAME_ATTRIBUTE = "someMemberNameAttribute"

    static final TEST_PROPERTY_FILE_LOCATION = "src/test/resources/testLdap.properties"

    def setup() {
        ldapClaimsHandlerServiceProperties = new LdapClaimsHandlerServiceProperties()
    }

    def 'test ldapClaimsHandlerServiceToLdapConfig(Map<String, Object>) all present values success'() {
        setup:
        def properties = [ValidationUtils.SERVICE_PID_KEY, TEST_SERVICE_PID,
                          LdapClaimsHandlerServiceProperties.URL, TEST_URL,
                          LdapClaimsHandlerServiceProperties.START_TLS, true,
                          LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN, TEST_BIND_USER_DN,
                          LdapClaimsHandlerServiceProperties.PASSWORD, TEST_PASSWORD,
                          LdapClaimsHandlerServiceProperties.BIND_METHOD, TEST_BIND_METHOD,
                          LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE, TEST_LOGIN_USER_ATTRIBUTE,
                          LdapClaimsHandlerServiceProperties.USER_BASE_DN, TEST_USER_BASE_DN,
                          LdapClaimsHandlerServiceProperties.GROUP_BASE_DN, TEST_GROUP_BASE_DN,
                          LdapClaimsHandlerServiceProperties.OBJECT_CLASS, TEST_OBJECT_CLASS,
                          LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE, TEST_MEMBERSHIP_USER_ATTRIBUTE,
                          LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE, TEST_MEMBER_NAME_ATTRIBUTE,
                          LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION, TEST_PROPERTY_FILE_LOCATION]
                .toSpreadMap()

        when:
        LdapConfiguration ldapConfiguration = ldapClaimsHandlerServiceProperties.ldapClaimsHandlerServiceToLdapConfig(properties)

        then:
        ldapConfiguration.servicePid() == TEST_SERVICE_PID
        ldapConfiguration.encryptionMethod() == LdapClaimsHandlerServiceProperties.START_TLS
        ldapConfiguration.hostName() == TEST_HOST_NAME
        ldapConfiguration.port() == TEST_PORT
        ldapConfiguration.bindUserDn() == TEST_BIND_USER_DN
        ldapConfiguration.bindUserPassword() == TEST_PASSWORD
        ldapConfiguration.bindUserMethod() == TEST_BIND_METHOD
        ldapConfiguration.userNameAttribute() == TEST_LOGIN_USER_ATTRIBUTE
        ldapConfiguration.baseUserDn() == TEST_USER_BASE_DN
        ldapConfiguration.baseGroupDn() == TEST_GROUP_BASE_DN
        ldapConfiguration.groupObjectClass() == TEST_OBJECT_CLASS
        ldapConfiguration.memberAttributeReferencedInGroup() == TEST_MEMBER_NAME_ATTRIBUTE
        ldapConfiguration.groupAttributeHoldingMember() == TEST_MEMBERSHIP_USER_ATTRIBUTE
        ldapConfiguration.ldapUseCase() == LdapValidationUtils.ATTRIBUTE_STORE

        ldapConfiguration.attributeMappings().get("testKey1") == "testValue1"
        ldapConfiguration.attributeMappings().get("testKey2") == "testValue2"
    }

    def 'test ldapClaimsHandlerServiceToLdapConfig(Map<String, Object>) all null values'() {
        setup:
        def properties = [
                LdapClaimsHandlerServiceProperties.URL, null,
                LdapClaimsHandlerServiceProperties.START_TLS, false,
                LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN, null,
                LdapClaimsHandlerServiceProperties.PASSWORD, null,
                LdapClaimsHandlerServiceProperties.BIND_METHOD, null,
                LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE, null,
                LdapClaimsHandlerServiceProperties.USER_BASE_DN, null,
                LdapClaimsHandlerServiceProperties.GROUP_BASE_DN, null,
                LdapClaimsHandlerServiceProperties.OBJECT_CLASS, null,
                LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE, null,
                LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE, null,
                LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION, null]
                .toSpreadMap()

        when:
        LdapConfiguration ldapConfiguration = ldapClaimsHandlerServiceProperties.ldapClaimsHandlerServiceToLdapConfig(properties)

        then:
        ldapConfiguration.servicePid() == null
        ldapConfiguration.encryptionMethod() == null
        ldapConfiguration.hostName() == null
        ldapConfiguration.port() == 0
        ldapConfiguration.bindUserDn() == null
        ldapConfiguration.bindUserPassword() == null
        ldapConfiguration.bindUserMethod() == null
        ldapConfiguration.userNameAttribute() == null
        ldapConfiguration.baseUserDn() == null
        ldapConfiguration.baseGroupDn() == null
        ldapConfiguration.groupObjectClass() == null
        ldapConfiguration.memberAttributeReferencedInGroup() == null
        ldapConfiguration.groupAttributeHoldingMember() == null
        ldapConfiguration.ldapUseCase() == LdapValidationUtils.ATTRIBUTE_STORE
        ldapConfiguration.attributeMappings() == null
    }

    def 'test ldapClaimsHandlerServiceToLdapConfig(Map<String, Object>) not startTls'() {
        setup:
        def properties = [
                LdapClaimsHandlerServiceProperties.URL, TEST_URL,
                LdapClaimsHandlerServiceProperties.START_TLS, false]
                .toSpreadMap()

        when:
        LdapConfiguration ldapConfiguration = ldapClaimsHandlerServiceProperties.ldapClaimsHandlerServiceToLdapConfig(properties)

        then:
        ldapConfiguration.encryptionMethod() == TEST_URI_SCHEME
        ldapConfiguration.hostName() == TEST_HOST_NAME
        ldapConfiguration.port() == TEST_PORT
    }

    def 'test ldapConfigToLdapClaimsHandlerService(LdapConfiguration) success'() {
        setup:
        LdapConfiguration ldapConfiguration = Mock(LdapConfiguration) {
            encryptionMethod() >> TEST_URI_SCHEME
            hostName() >> TEST_HOST_NAME
            port() >> TEST_PORT
            bindUserDn() >> TEST_BIND_USER_DN
            bindUserPassword() >> TEST_PASSWORD
            bindUserMethod() >> TEST_BIND_METHOD
            userNameAttribute() >> TEST_LOGIN_USER_ATTRIBUTE
            baseUserDn() >> TEST_USER_BASE_DN
            baseGroupDn() >> TEST_GROUP_BASE_DN
            groupObjectClass() >> TEST_OBJECT_CLASS
            memberAttributeReferencedInGroup() >> TEST_MEMBERSHIP_USER_ATTRIBUTE
            groupAttributeHoldingMember() >> TEST_MEMBER_NAME_ATTRIBUTE
            attributeMappingsPath() >> TEST_PROPERTY_FILE_LOCATION
        }

        when:
        Map result = ldapClaimsHandlerServiceProperties.ldapConfigToLdapClaimsHandlerService(ldapConfiguration)

        then:
        result.get(LdapClaimsHandlerServiceProperties.URL) == TEST_URL
        result.get(LdapClaimsHandlerServiceProperties.START_TLS) == false
        result.get(LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN) == TEST_BIND_USER_DN
        result.get(LdapClaimsHandlerServiceProperties.PASSWORD) == TEST_PASSWORD
        result.get(LdapClaimsHandlerServiceProperties.BIND_METHOD) == TEST_BIND_METHOD
        result.get(LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE) == TEST_LOGIN_USER_ATTRIBUTE
        result.get(LdapClaimsHandlerServiceProperties.USER_BASE_DN) == TEST_USER_BASE_DN
        result.get(LdapClaimsHandlerServiceProperties.GROUP_BASE_DN) == TEST_GROUP_BASE_DN
        result.get(LdapClaimsHandlerServiceProperties.OBJECT_CLASS) == TEST_OBJECT_CLASS
        result.get(LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE) == TEST_MEMBERSHIP_USER_ATTRIBUTE
        result.get(LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE) == TEST_MEMBER_NAME_ATTRIBUTE
        result.get(LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION) == TEST_PROPERTY_FILE_LOCATION
    }

    def 'test ldapConfigToLdapClaimsHandlerService(LdapConfiguration) null LdapConfiguration'() {
        when:
        Map result = ldapClaimsHandlerServiceProperties.ldapConfigToLdapClaimsHandlerService(null)

        then:
        result == Collections.emptyMap()
    }
}