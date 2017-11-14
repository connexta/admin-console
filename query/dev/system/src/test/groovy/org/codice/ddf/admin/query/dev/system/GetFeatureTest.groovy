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

import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.query.dev.system.dependency.FeatureUtils
import org.codice.ddf.admin.query.dev.system.discover.GetFeatures
import spock.lang.Specification

class GetFeatureTest extends Specification {

    def "Get all features"() {
        setup:
        FeatureUtils featureUtils = Mock(FeatureUtils)
        def featureA = SampleData.featureA()
        def featureB = SampleData.featureB()
        def featureC = SampleData.featureC()
        featureUtils.getAllFeatures() >> [featureA, featureB, featureC]

        when:
        Report report = new GetFeatures(featureUtils).execute(null, null)

        then:
        report.getResult().getValue() == [featureA.getValue(), featureB.getValue(), featureC.getValue()]
    }
}
