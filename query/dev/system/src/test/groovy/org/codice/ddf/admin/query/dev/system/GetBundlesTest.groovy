/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.query.dev.system

import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils
import org.codice.ddf.admin.query.dev.system.discover.GetBundles
import org.codice.ddf.admin.query.dev.system.fields.BundleField
import spock.lang.Specification

class GetBundlesTest extends Specification {

    private BundleUtils bundleUtils

    def setup() {
        bundleUtils = Mock(BundleUtils)
    }

    def "Get all bundles"() {
        setup:
        def bundleA = SampleData.bundleA()
        def bundleB = SampleData.bundleB()
        def bundleC = SampleData.bundleC()
        bundleUtils.getAllBundleFields() >> [bundleA, bundleB, bundleC]

        when:
        Report report = new GetBundles(bundleUtils).execute([:], [])

        then:
        report.getResult().getValue() == [bundleA.getValue(), bundleB.getValue(), bundleC.getValue()]
    }

    def "Bundle id arg is passed properly"() {
        setup:
        List<Integer> bundlesIds = [0, 1, 2]

        when:
        Report<ListField<BundleField>> report = new GetBundles(bundleUtils).execute([(GetBundles.BUNDLE_IDS): bundlesIds], [GetBundles.FIELD_NAME])

        then:
        report.getResult().getValue().isEmpty()
        1 * bundleUtils.getBundles(bundlesIds) >> []
    }
}
