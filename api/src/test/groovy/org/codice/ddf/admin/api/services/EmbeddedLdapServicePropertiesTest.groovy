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

import org.codice.ddf.admin.api.config.ldap.EmbeddedLdapConfiguration
import spock.lang.Specification

class EmbeddedLdapServicePropertiesTest extends Specification {

    EmbeddedLdapServiceProperties embeddedLdapServiceProperties

    def setup() {
        embeddedLdapServiceProperties = new EmbeddedLdapServiceProperties()
    }

    def 'test embeddedLdapServiceToEmbeddedLdapConfig(Map<String, Object> props) all attributes present'() {
        setup:
        def properties = [EmbeddedLdapConfiguration.EMBEDDED_LDAP_PORT, 10,
                          EmbeddedLdapConfiguration.EMBEDDED_LDAPS_PORT, 11,
                          EmbeddedLdapConfiguration.EMBEDDED_LDAP_ADMIN_PORT, 12,
                          EmbeddedLdapConfiguration.LDIF_PATH, "/ldif/path",
                          EmbeddedLdapConfiguration.EMBEDDED_LDAP_STORAGE_LOC, "/storage/path"]
                .toSpreadMap()

        when:
        EmbeddedLdapConfiguration embeddedLdapConfiguration = embeddedLdapServiceProperties.embeddedLdapServiceToEmbeddedLdapConfig(properties)
        def strResult = embeddedLdapConfiguration.toString()

        then:
        strResult.contains("10")
        strResult.contains("11")
        strResult.contains("12")
        strResult.contains("/ldif/path")
        strResult.contains("/storage/path")
    }

    def 'test embeddedLdapServiceToEmbeddedLdapConfig(Map<String, Object> props) null attributest'() {
        setup:
        def properties = [EmbeddedLdapConfiguration.EMBEDDED_LDAP_PORT, null,
                          EmbeddedLdapConfiguration.EMBEDDED_LDAPS_PORT, null,
                          EmbeddedLdapConfiguration.EMBEDDED_LDAP_ADMIN_PORT, null,
                          EmbeddedLdapConfiguration.LDIF_PATH, null,
                          EmbeddedLdapConfiguration.EMBEDDED_LDAP_STORAGE_LOC, null]
                .toSpreadMap()

        when:
        EmbeddedLdapConfiguration embeddedLdapConfiguration = embeddedLdapServiceProperties.embeddedLdapServiceToEmbeddedLdapConfig(properties)
        def strResult = embeddedLdapConfiguration.toString()

        then:
        strResult.contains(EmbeddedLdapConfiguration.EMBEDDED_LDAP_PORT + "=-1")
        strResult.contains(EmbeddedLdapConfiguration.EMBEDDED_LDAPS_PORT + "=-1")
        strResult.contains(EmbeddedLdapConfiguration.EMBEDDED_LDAP_ADMIN_PORT + "=-1")
        strResult.contains(EmbeddedLdapConfiguration.LDIF_PATH + "=null")
        strResult.contains(EmbeddedLdapConfiguration.EMBEDDED_LDAP_STORAGE_LOC + "=null")
    }
}