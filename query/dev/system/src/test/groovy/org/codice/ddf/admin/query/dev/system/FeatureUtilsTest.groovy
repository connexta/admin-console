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

import org.apache.karaf.features.FeaturesService
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils
import org.codice.ddf.admin.query.dev.system.dependency.FeatureUtils
import org.codice.ddf.admin.query.dev.system.fields.BundleField
import org.codice.ddf.admin.query.dev.system.fields.FeatureField
import spock.lang.Specification

class FeatureUtilsTest extends Specification {

    private SampleData sampleData
    private BundleUtils bundleUtils
    private FeaturesService featuresService
    private FeatureUtils featureUtils

    def setup() {
        bundleUtils = Mock(BundleUtils)
        sampleData = new SampleData()
        featuresService = Mock(FeaturesService)
        featureUtils = new FeatureUtils(bundleUtils, featuresService)
        bundleUtils.getAllBundleFields() >> [SampleData.bundleA(), SampleData.bundleB(), SampleData.bundleC()]
    }

    def "Populate feature information correctly"() {
        setup:
        def featA = SampleData.featureA()
        def featB = SampleData.featureB()
        def featC = SampleData.featureC()
        featuresService.listFeatures() >> sampleData.featureFieldsToFeatures([featA, featB, featC])

        when:
        List<FeatureField> result = featureUtils.getAllFeatures()

        then:
        result.size() == 3
        result.get(0).getValue() == featA.getValue()
        result.get(1).getValue() == featB.getValue()
        result.get(2).getValue() == featC.getValue()
    }

    def "Include bundles that are not installed in the system"() {
        setup:
        def featC = SampleData.featureC()
        featC.addBundleDeps([new BundleField().location("does_not_exist")])
        featuresService.listFeatures() >> sampleData.featureFieldsToFeatures([featC])

        when:
        List<FeatureField> result = featureUtils.getAllFeatures()

        then:
        result.size() == 1
        result.get(0).getValue() == featC.getValue()
    }
}
