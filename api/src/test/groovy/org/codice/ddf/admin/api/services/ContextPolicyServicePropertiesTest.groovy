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

import org.codice.ddf.admin.api.config.context.ContextPolicyBin
import org.codice.ddf.admin.api.config.context.ContextPolicyConfiguration
import spock.lang.Specification

import static org.codice.ddf.admin.api.services.ContextPolicyServiceProperties.*

class ContextPolicyServicePropertiesTest extends Specification {

    private static final TEST_CONTEXT = "testContext"
    private static final TEST_REALM = "testRealm"
    private static final TEST_AUTH_TYPES = ["testAuthType1", "testAuthType2"]
    private static final TEST_REQ_ATTR = [a1: "testAttr1", a2: "testAttr2", a3: "testAttr3"]

    def 'test config with no context policy bins to props'() {
        setup:
        def config = Mock(ContextPolicyConfiguration)

        when:
        def props = configToPolicyManagerProps(config)

        then:
        1 * config.contextPolicyBins() >> []
        1 * config.whiteListContexts() >> []
        props.get(AUTH_TYPES) == []
        props.get(REALMS) == []
        props.get(REQUIRED_ATTRIBUTES) == []
        props.get(WHITE_LIST_CONTEXT) == []
    }

    def 'test simple context policy with required attributes'() {
        setup:
        def config = Mock(ContextPolicyConfiguration)
        def bin = Mock(ContextPolicyBin)

        when:
        def props = configToPolicyManagerProps(config)

        then:
        1 * config.contextPolicyBins() >> [bin]
        1 * bin.contextPaths() >> [TEST_CONTEXT]
        1 * bin.realm() >> TEST_REALM
        1 * bin.authenticationTypes() >> TEST_AUTH_TYPES
        2 * bin.requiredAttributes() >> TEST_REQ_ATTR
        1 * config.whiteListContexts() >> []

        props.get(AUTH_TYPES) == ([TEST_CONTEXT + "=" + TEST_AUTH_TYPES.get(0) + "|" + TEST_AUTH_TYPES.get(1)])

        props.get(REALMS) == [TEST_CONTEXT + "=" + TEST_REALM]

        props.get(REQUIRED_ATTRIBUTES) == [TEST_CONTEXT + "={a1=testAttr1;a2=testAttr2;a3=testAttr3}"]
    }

    def 'test no required attributes'() {
        setup:
        def config = Mock(ContextPolicyConfiguration)
        def bin = Mock(ContextPolicyBin)

        when:
        def props = configToPolicyManagerProps(config)

        then:
        1 * config.contextPolicyBins() >> [bin]
        1 * bin.contextPaths() >> [TEST_CONTEXT]
        1 * bin.realm() >> TEST_REALM
        1 * bin.authenticationTypes() >> TEST_AUTH_TYPES
        1 * bin.requiredAttributes() >> new HashMap()
        1 * config.whiteListContexts() >> []
        props.get(REQUIRED_ATTRIBUTES) == [TEST_CONTEXT + "="]
    }
}