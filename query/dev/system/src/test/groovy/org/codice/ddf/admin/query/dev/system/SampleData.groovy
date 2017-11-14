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

import org.apache.karaf.features.BundleInfo
import org.apache.karaf.features.Dependency
import org.apache.karaf.features.Feature
import org.codice.ddf.admin.query.dev.system.fields.*
import org.osgi.framework.Bundle
import spock.lang.Specification

import java.util.stream.Collectors

class SampleData extends Specification {

    static final String EXAMPLE_INTERFACE = "example_interface"

    static final String BUNDLE_A_NAME = "bundle_A"
    static final int BUNDLE_A_ID = 0
    static final String BUNDLE_A_LOCATION = "bundle_location_A"
    static final String BUNDLE_A_PKG = "pkg_A"
    static final String BUNDLE_A_SERVICE_NAME = "service_A"
    static final String BUNDLE_A_SERVICE_ID = "service_id_A"
    static final String BUNDLE_A_STATE = Bundle.UNINSTALLED

    static final String BUNDLE_B_NAME = "bundle_B"
    static final int BUNDLE_B_ID = 1
    static final String BUNDLE_B_LOCATION = "bundle_location_B"
    static final String BUNDLE_B_PKG = "pkg_B"
    static final String BUNDLE_B_SERVICE_NAME = "service_B"
    static final String BUNDLE_B_SERVICE_ID = "service_id_B"
    static final String BUNDLE_B_STATE = Bundle.INSTALLED

    static final String BUNDLE_C_NAME = "bundle_C"
    static final int BUNDLE_C_ID = 2
    static final String BUNDLE_C_LOCATION = "bundle_location_C"
    static final String BUNDLE_C_PKG = "pkg_C"
    static final String BUNDLE_C_PKG_2 = "pkg_C_2"
    static final String BUNDLE_C_SERVICE_NAME = "service_C"
    static final String BUNDLE_C_SERVICE_ID = "service_id_C"
    static final String BUNDLE_C_SERVICE_NAME_2 = "service_C_2"
    static final String BUNDLE_C_SERVICE_ID_2 = "service_id_C_2"
    static final String BUNDLE_C_STATE = Bundle.RESOLVED

    static final String FEATURE_A_NAME = "feature_A_name"
    static final String FEATURE_A_DESCRIPTION = "feature_A_description"
    static final String FEATURE_A_STATE = "feature_A_state"
    static final String FEATURE_A_ID = "feature_A_id"
    static final String FEATURE_A_REPO_URL = "feature_A_repo_url"

    static final String FEATURE_B_NAME = "feature_B_name"
    static final String FEATURE_B_DESCRIPTION = "feature_B_description"
    static final String FEATURE_B_STATE = "feature_B_state"
    static final String FEATURE_B_ID = "feature_B_id"
    static final String FEATURE_B_REPO_URL = "feature_B_repo_url"

    static final String FEATURE_C_NAME = "feature_C_name"
    static final String FEATURE_C_DESCRIPTION = "feature_C_description"
    static final String FEATURE_C_STATE = "feature_C_state"
    static final String FEATURE_C_ID = "feature_C_id"
    static final String FEATURE_C_REPO_URL = "feature_C_repo_url"

    static PackageField pkgA() {
        return new PackageField().pkgName(BUNDLE_A_PKG).bundleId(BUNDLE_A_ID)
    }

    static PackageField pkgB() {
        return new PackageField().pkgName(BUNDLE_B_PKG).bundleId(BUNDLE_B_ID)
    }

    static PackageField pkgC() {
        return new PackageField().pkgName(BUNDLE_C_PKG).bundleId(BUNDLE_C_ID)
    }

    static PackageField pkgC2() {
        return new PackageField().pkgName(BUNDLE_C_PKG_2).bundleId(BUNDLE_C_ID)
    }

    static ServiceField serviceA() {
        return new ServiceField().serviceName(BUNDLE_A_SERVICE_NAME).bundleId(BUNDLE_A_ID)
    }

    static ServiceReferenceField serviceARef() {
        return new ServiceReferenceField().filter(BUNDLE_A_SERVICE_ID).resolution(0).serviceInterface(EXAMPLE_INTERFACE).service(serviceA())
    }

    static ServiceReferenceField serviceBRef() {
        return new ServiceReferenceField().filter(BUNDLE_B_SERVICE_ID).resolution(0).serviceInterface(EXAMPLE_INTERFACE).service(serviceB())
    }

    static ServiceReferenceField serviceCRef() {
        return new ServiceReferenceField().filter(BUNDLE_C_SERVICE_ID).resolution(0).serviceInterface(EXAMPLE_INTERFACE).service(serviceC())
    }

    static ServiceReferenceField serviceC2Ref() {
        return new ServiceReferenceField().filter(BUNDLE_C_SERVICE_ID_2).resolution(0).serviceInterface(EXAMPLE_INTERFACE).service(serviceC2())
    }

    static ServiceReferenceListField serviceCRefList() {
        return new ServiceReferenceListField().filter(BUNDLE_A_SERVICE_ID + "," + BUNDLE_B_SERVICE_ID).resolution(0).referenceListInterface(EXAMPLE_INTERFACE).addService(serviceA()).addService(serviceB())
    }

    static ServiceField serviceB() {
        return new ServiceField().serviceName(BUNDLE_B_SERVICE_NAME).bundleId(BUNDLE_B_ID)
    }

    static ServiceField serviceC() {
        return new ServiceField().serviceName(BUNDLE_C_SERVICE_NAME).bundleId(BUNDLE_B_ID)
    }

    static ServiceField serviceC2() {
        return new ServiceField().serviceName(BUNDLE_C_SERVICE_NAME_2).bundleId(BUNDLE_B_ID)
    }

    static BundleField bundleA() {
        return new BundleField().bundleName(BUNDLE_A_NAME)
                .id(BUNDLE_A_ID)
                .location(BUNDLE_A_LOCATION)
                .state(BUNDLE_A_STATE)
                .addExportedPackage(pkgA())
                .addService(serviceA())
    }

    static BundleField bundleB() {
        return new BundleField().bundleName(BUNDLE_B_NAME)
                .id(BUNDLE_B_ID)
                .location(BUNDLE_B_LOCATION)
                .state(BUNDLE_B_STATE)
                .addExportedPackage(pkgB())
                .addImportedPackage(pkgA())
                .addService(serviceB())
                .addServiceRef(serviceARef())
    }

    static BundleField bundleC() {
        return new BundleField().bundleName(BUNDLE_C_NAME)
                .id(BUNDLE_C_ID)
                .location(BUNDLE_C_LOCATION)
                .state(BUNDLE_C_STATE)
                .addExportedPackage(pkgC())
                .addExportedPackage(pkgC2())
                .addImportedPackage(pkgA())
                .addImportedPackage(pkgB())
                .addService(serviceC())
                .addService(serviceC2())
                .addServiceRef(serviceARef())
                .addServiceRef(serviceBRef())
                .addServiceRefList(serviceCRefList())
    }

    static FeatureField featureA() {
        return new FeatureField()
                .name(FEATURE_A_NAME)
                .id(FEATURE_A_ID)
                .featDescription(FEATURE_A_DESCRIPTION)
                .state(FEATURE_A_STATE)
                .repoUrl(FEATURE_A_REPO_URL)
                .addBundleDeps([bundleA()])
    }

    static FeatureField featureB() {
        return new FeatureField()
                .name(FEATURE_B_NAME)
                .id(FEATURE_B_ID)
                .featDescription(FEATURE_B_DESCRIPTION)
                .state(FEATURE_B_STATE)
                .repoUrl(FEATURE_B_REPO_URL)
                .addBundleDeps([bundleA(), bundleB()])
                .addFeatureDeps([FEATURE_A_ID])
    }

    static FeatureField featureC() {
        return new FeatureField()
                .name(FEATURE_C_NAME)
                .id(FEATURE_C_ID)
                .featDescription(FEATURE_C_DESCRIPTION)
                .state(FEATURE_C_STATE)
                .repoUrl(FEATURE_C_REPO_URL)
                .addBundleDeps([bundleA(), bundleB(), bundleC()])
                .addFeatureDeps([FEATURE_A_ID, FEATURE_B_ID])
    }

    Feature[] featureFieldsToFeatures(List<FeatureField> fields) {
        return fields.stream().map({ field -> featureFieldToFeature(field) }).collect(Collectors.toList())
    }

    Feature featureFieldToFeature(FeatureField field) {
        Feature feat = Mock(Feature)
        feat.getName() >> field.name()
        feat.getDescription() >> field.featDescription()
        feat.getInstall() >> field.state()
        feat.getId() >> field.id()
        feat.getRepositoryUrl() >> field.repoUrl()

        List<BundleInfo> bundleInfos = []
        for (BundleField bundleField : field.bundleDeps()) {
            BundleInfo bundleInfo = Mock(BundleInfo)
            bundleInfo.getLocation() >> bundleField.location()
            bundleInfos.add(bundleInfo)
        }

        List<Dependency> featureDeps = []
        for (String featureId : field.featDeps()) {
            Dependency dep = Mock(Dependency)
            dep.getName() >> featureId
            featureDeps.add(dep)
        }

        feat.getBundles() >> bundleInfos
        feat.getDependencies() >> featureDeps
        return feat
    }
}
