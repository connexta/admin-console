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
package org.codice.ddf.admin.security.ldap

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.security.ldap.persist.CreateLdapConfigMethod
import org.codice.ddf.admin.security.ldap.persist.DeleteLdapConfigMethod
import org.codice.ddf.admin.security.ldap.probe.DefaultDirectoryStructureProbe
import org.codice.ddf.admin.security.ldap.probe.LdapQueryProbe
import org.codice.ddf.admin.security.ldap.probe.SubjectAttributeProbe
import org.codice.ddf.admin.security.ldap.test.AttributeMappingTestMethod
import org.codice.ddf.admin.security.ldap.test.BindUserTestMethod
import org.codice.ddf.admin.security.ldap.test.ConnectTestMethod
import org.codice.ddf.admin.security.ldap.test.DirectoryStructTestMethod
import spock.lang.Specification

class LdapConfigurationHandlerTest extends Specification {
    LdapConfigurationHandler handler

    def setup() {
        handler = new LdapConfigurationHandler()
    }

    def 'test config handler id'() {
        expect:
        handler.configurationHandlerId == LdapConfiguration.CONFIGURATION_TYPE
    }

    def 'getConfigurationType() is LDAP configuration type'() {
        when:
        def configurationType = handler.getConfigurationType()

        then:
        configurationType.configTypeName() == LdapConfiguration.CONFIGURATION_TYPE
        configurationType.configClass() == LdapConfiguration.class
    }

    def 'test methods available'() {
        when:
        def testMethods = handler.getTestMethods()
        def probeMethods = handler.getProbeMethods()
        def persistMethods = handler.getPersistMethods()

        then:
        testMethods*.class == [ConnectTestMethod, BindUserTestMethod, DirectoryStructTestMethod, AttributeMappingTestMethod]
        probeMethods*.class == [DefaultDirectoryStructureProbe, LdapQueryProbe, SubjectAttributeProbe]
        persistMethods*.class == [CreateLdapConfigMethod, DeleteLdapConfigMethod]
    }
}
