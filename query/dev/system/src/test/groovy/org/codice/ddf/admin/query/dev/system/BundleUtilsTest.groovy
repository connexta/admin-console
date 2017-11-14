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

import org.apache.felix.utils.collections.MapToDictionary
import org.apache.karaf.bundle.core.BundleService
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils
import org.codice.ddf.admin.query.dev.system.fields.BundleField
import org.codice.ddf.admin.query.dev.system.fields.PackageField
import org.codice.ddf.admin.query.dev.system.fields.ServiceReferenceField
import org.osgi.framework.Bundle
import org.osgi.framework.InvalidSyntaxException
import org.osgi.framework.ServiceReference
import org.osgi.service.blueprint.container.BlueprintContainer
import org.osgi.service.blueprint.reflect.ComponentMetadata
import org.osgi.service.blueprint.reflect.ReferenceListMetadata
import org.osgi.service.blueprint.reflect.ReferenceMetadata
import org.osgi.service.blueprint.reflect.ServiceMetadata
import spock.lang.Specification

import static org.codice.ddf.admin.query.dev.system.SampleData.*


class BundleUtilsTest extends Specification {

    BundleUtils bundleUtils
    Map<Bundle, Map<String, Bundle>> bundleToPkgDepMap
    List<Bundle> allMockBundles
    Map<Bundle, MockBlueprintContainer> allBlueprintContainers
    Map<String, ServiceReference> allServiceRefs
    Map<ServiceReference, Object> allServices
    Map<Bundle, List<ServiceReference>> bundleToRegisteredServices

    static final String NO_EXISTING_SERVICE_ID = "no_existing_service_id"


    static final
    def MATCH_PKG = { PackageField pkg, String name, int id -> return pkg.pkgName() == name && pkg.bundleId() == id }

    def setup() {
        bundleToPkgDepMap = new HashMap<>()
        allMockBundles = []
        allBlueprintContainers = new HashMap<>()
        allServiceRefs = new HashMap<>()
        allServices = new HashMap<>()
        bundleToRegisteredServices = new HashMap<>()

        def mockBundleService = Mock(BundleService)
        mockBundleService.getWiredBundles(_) >> {
            Bundle bundle -> bundleToPkgDepMap.get(bundle)
        }

        bundleUtils = new BundleUtils(mockBundleService) {
            @Override
            protected <S> S getService(ServiceReference<S> ref) {
                return allServices.get(ref)
            }

            @Override
            protected Optional<BlueprintContainer> getBlueprintContainer(Bundle bundle) {
                return allBlueprintContainers.containsKey(bundle) ? Optional.of(allBlueprintContainers.get(bundle)) : Optional.empty()
            }

            @Override
            protected List<ServiceReference> getServiceReferences(String refInterface, String filter) throws InvalidSyntaxException {
                //Used filter to pass the ids needed to match
                List<ServiceReference> matchedRefs = []
                List<String> refIds = filter.substring(1, filter.length() - 1).split(",")
                for (String mockRefId : refIds) {
                    if (allServiceRefs.containsKey(mockRefId)) {
                        matchedRefs.add(allServiceRefs.get(mockRefId))
                    }
                }
                return matchedRefs
            }

            @Override
            protected List<ServiceReference<?>> getRegisteredServices(Bundle bundle) {
                return bundleToRegisteredServices.containsKey(bundle) ? bundleToRegisteredServices.get(bundle) : Collections.emptyList()
            }

            @Override
            protected List<Bundle> getAllBundles() {
                return allMockBundles
            }
        }
    }

    def "Associates packages between bundles correctly"() {
        setup:
        Bundle bundleA = addMockBundle(BUNDLE_A_NAME, BUNDLE_A_ID, BUNDLE_A_LOCATION, [BUNDLE_A_PKG], [:])
        Bundle bundleB = addMockBundle(BUNDLE_B_NAME, BUNDLE_B_ID, BUNDLE_B_LOCATION, [BUNDLE_B_PKG], [(BUNDLE_A_PKG): bundleA])
        Bundle bundleC = addMockBundle(BUNDLE_C_NAME, BUNDLE_C_ID, BUNDLE_C_LOCATION, [BUNDLE_C_PKG, BUNDLE_C_PKG_2], [(BUNDLE_A_PKG): bundleA, (BUNDLE_B_PKG): bundleB])

        when:
        List<BundleField> result = bundleUtils.getAllBundleFields()
        Optional<BundleField> bundleAResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_A_NAME) }).findFirst()
        Optional<BundleField> bundleBResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_B_NAME) }).findFirst()
        Optional<BundleField> bundleCResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_C_NAME) }).findFirst()


        then:
        result.size() == 3

        bundleAResult.isPresent()
        bundleAResult.get().bundleName() == BUNDLE_A_NAME
        bundleAResult.get().location() == BUNDLE_A_LOCATION
        bundleAResult.get().id() == BUNDLE_A_ID

        bundleAResult.get().exportedPackages().size() == 1
        bundleAResult.get().exportedPackages().get(0).pkgName() == BUNDLE_A_PKG
        bundleAResult.get().exportedPackages().get(0).bundleId() == BUNDLE_A_ID

        bundleBResult.isPresent()
        bundleBResult.get().bundleName() == BUNDLE_B_NAME
        bundleBResult.get().location() == BUNDLE_B_LOCATION
        bundleBResult.get().id() == BUNDLE_B_ID

        bundleBResult.get().exportedPackages().size() == 1
        bundleBResult.get().exportedPackages().get(0).pkgName() == BUNDLE_B_PKG
        bundleBResult.get().exportedPackages().get(0).bundleId() == BUNDLE_B_ID

        bundleBResult.get().importedPackages().size() == 1
        bundleBResult.get().importedPackages().get(0).pkgName() == BUNDLE_A_PKG
        bundleBResult.get().importedPackages().get(0).bundleId() == BUNDLE_A_ID

        bundleCResult.isPresent()
        bundleCResult.get().bundleName() == BUNDLE_C_NAME
        bundleCResult.get().location() == BUNDLE_C_LOCATION
        bundleCResult.get().id() == BUNDLE_C_ID

        bundleCResult.get().exportedPackages().size() == 2
        bundleCResult.get().exportedPackages().stream().filter({ pkg -> MATCH_PKG(pkg, BUNDLE_C_PKG, BUNDLE_C_ID) }).findFirst().isPresent()
        bundleCResult.get().exportedPackages().stream().filter({ pkg -> MATCH_PKG(pkg, BUNDLE_C_PKG_2, BUNDLE_C_ID) }).findFirst().isPresent()

        bundleCResult.get().importedPackages().size() == 2
        bundleCResult.get().importedPackages().stream().filter({ pkg -> MATCH_PKG(pkg, BUNDLE_A_PKG, BUNDLE_A_ID) }).findFirst().isPresent()
        bundleCResult.get().importedPackages().stream().filter({ pkg -> MATCH_PKG(pkg, BUNDLE_B_PKG, BUNDLE_B_ID) }).findFirst().isPresent()
    }

    def "Associates service references between bundles correctly"() {
        setup:
        Bundle bundleA = addMockBundle(BUNDLE_A_NAME, BUNDLE_A_ID, BUNDLE_A_LOCATION, [BUNDLE_A_PKG], [:])
        Bundle bundleB = addMockBundle(BUNDLE_B_NAME, BUNDLE_B_ID, BUNDLE_B_LOCATION, [BUNDLE_B_PKG], [(BUNDLE_A_PKG): bundleA])
        Bundle bundleC = addMockBundle(BUNDLE_C_NAME, BUNDLE_C_ID, BUNDLE_C_LOCATION, [BUNDLE_C_PKG, BUNDLE_C_PKG_2], [(BUNDLE_A_PKG): bundleA, (BUNDLE_B_PKG): bundleB])

        registerService(bundleA, BUNDLE_A_SERVICE_ID, BUNDLE_A_SERVICE_NAME)
        registerService(bundleB, BUNDLE_B_SERVICE_ID, BUNDLE_B_SERVICE_NAME)
        registerService(bundleC, BUNDLE_C_SERVICE_ID, BUNDLE_C_SERVICE_NAME)
        registerService(bundleC, BUNDLE_C_SERVICE_ID_2, BUNDLE_C_SERVICE_NAME_2)

        addServiceDependency(bundleB, BUNDLE_A_SERVICE_ID, 0)
        addServiceDependency(bundleC, BUNDLE_A_SERVICE_ID, 1)
        addServiceDependency(bundleC, BUNDLE_B_SERVICE_ID, 0)

        when:
        List<BundleField> result = bundleUtils.getAllBundleFields()
        Optional<BundleField> bundleAResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_A_NAME) }).findFirst()
        Optional<BundleField> bundleBResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_B_NAME) }).findFirst()
        Optional<BundleField> bundleCResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_C_NAME) }).findFirst()

        then:
        result.size() == 3
        bundleAResult.isPresent()
        bundleAResult.get().services().size() == 1
        bundleAResult.get().services().get(0).serviceName() == BUNDLE_A_SERVICE_NAME
        bundleAResult.get().services().get(0).bundleId() == BUNDLE_A_ID
        bundleAResult.get().serviceRefs().isEmpty()
        bundleAResult.get().serviceRefLists().isEmpty()

        bundleBResult.isPresent()
        bundleBResult.get().services().size() == 1
        bundleBResult.get().services().get(0).serviceName() == BUNDLE_B_SERVICE_NAME
        bundleBResult.get().services().get(0).bundleId() == BUNDLE_B_ID

        bundleBResult.get().serviceRefs().size() == 1
        bundleBResult.get().serviceRefs().get(0).serviceInterface() == EXAMPLE_INTERFACE
        bundleBResult.get().serviceRefs().get(0).filter() == "(" + BUNDLE_A_SERVICE_ID + ")"
        bundleBResult.get().serviceRefs().get(0).resolution() == ServiceReferenceField.OPTIONAL
        bundleBResult.get().serviceRefs().get(0).service().bundleId() == BUNDLE_A_ID
        bundleBResult.get().serviceRefs().get(0).service().serviceName() == BUNDLE_A_SERVICE_NAME
        bundleBResult.get().serviceRefLists().isEmpty()

        bundleCResult.isPresent()
        bundleCResult.get().services().size() == 2
        bundleCResult.get().services().get(0).serviceName() == BUNDLE_C_SERVICE_NAME
        bundleCResult.get().services().get(0).bundleId() == BUNDLE_C_ID

        bundleCResult.get().services().get(1).serviceName() == BUNDLE_C_SERVICE_NAME_2
        bundleCResult.get().services().get(1).bundleId() == BUNDLE_C_ID

        bundleCResult.get().serviceRefs().size() == 2
        bundleCResult.get().serviceRefs().get(0).serviceInterface() == EXAMPLE_INTERFACE
        bundleCResult.get().serviceRefs().get(0).filter() == "(" + BUNDLE_A_SERVICE_ID + ")"
        bundleCResult.get().serviceRefs().get(0).resolution() == ServiceReferenceField.MANDATORY
        bundleCResult.get().serviceRefs().get(0).service().bundleId() == BUNDLE_A_ID
        bundleCResult.get().serviceRefs().get(0).service().serviceName() == BUNDLE_A_SERVICE_NAME

        bundleCResult.get().serviceRefs().get(1).serviceInterface() == EXAMPLE_INTERFACE
        bundleCResult.get().serviceRefs().get(1).filter() == "(" + BUNDLE_B_SERVICE_ID + ")"
        bundleCResult.get().serviceRefs().get(1).resolution() == ServiceReferenceField.OPTIONAL
        bundleCResult.get().serviceRefs().get(1).service().bundleId() == BUNDLE_B_ID
        bundleCResult.get().serviceRefs().get(1).service().serviceName() == BUNDLE_B_SERVICE_NAME
    }

    def "Associates service reference lists correctly"() {
        setup:
        Bundle bundleA = addMockBundle(BUNDLE_A_NAME, BUNDLE_A_ID, BUNDLE_A_LOCATION, [BUNDLE_A_PKG], [:])
        Bundle bundleB = addMockBundle(BUNDLE_B_NAME, BUNDLE_B_ID, BUNDLE_B_LOCATION, [BUNDLE_B_PKG], [(BUNDLE_A_PKG): bundleA])
        Bundle bundleC = addMockBundle(BUNDLE_C_NAME, BUNDLE_C_ID, BUNDLE_C_LOCATION, [BUNDLE_C_PKG, BUNDLE_C_PKG_2], [(BUNDLE_A_PKG): bundleA, (BUNDLE_B_PKG): bundleB])

        registerService(bundleA, BUNDLE_A_SERVICE_ID, BUNDLE_A_SERVICE_NAME)
        registerService(bundleB, BUNDLE_B_SERVICE_ID, BUNDLE_B_SERVICE_NAME)

        addServiceListDependency(bundleC, [BUNDLE_A_SERVICE_ID, BUNDLE_B_SERVICE_ID], 0)

        when:
        List<BundleField> result = bundleUtils.getAllBundleFields()
        Optional<BundleField> bundleCResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_C_NAME) }).findFirst()

        then:
        result.size() == 3
        bundleCResult.isPresent()
        bundleCResult.get().serviceRefLists().size() == 1
        bundleCResult.get().serviceRefLists().get(0).referenceListInterface() == EXAMPLE_INTERFACE
        bundleCResult.get().serviceRefLists().get(0).filter() == "(" + [BUNDLE_A_SERVICE_ID, BUNDLE_B_SERVICE_ID].join(",") + ")"
        bundleCResult.get().serviceRefLists().get(0).resolution() == ServiceReferenceField.OPTIONAL
        bundleCResult.get().serviceRefLists().get(0).services().size() == 2
        bundleCResult.get().serviceRefLists().get(0).services().get(0).serviceName() == BUNDLE_A_SERVICE_NAME
        bundleCResult.get().serviceRefLists().get(0).services().get(0).bundleId() == BUNDLE_A_ID
        bundleCResult.get().serviceRefLists().get(0).services().get(1).serviceName() == BUNDLE_B_SERVICE_NAME
        bundleCResult.get().serviceRefLists().get(0).services().get(1).bundleId() == BUNDLE_B_ID
    }

    def "Skip service reference that can't be found"() {
        setup:
        Bundle bundleA = addMockBundle(BUNDLE_A_NAME, BUNDLE_A_ID, BUNDLE_A_LOCATION, [BUNDLE_A_PKG], [:])
        Bundle bundleB = addMockBundle(BUNDLE_B_NAME, BUNDLE_B_ID, BUNDLE_B_LOCATION, [BUNDLE_B_PKG], [(BUNDLE_A_PKG): bundleA])
        Bundle bundleC = addMockBundle(BUNDLE_C_NAME, BUNDLE_C_ID, BUNDLE_C_LOCATION, [BUNDLE_C_PKG, BUNDLE_C_PKG_2], [(BUNDLE_A_PKG): bundleA, (BUNDLE_B_PKG): bundleB])

        registerService(bundleA, BUNDLE_A_SERVICE_ID, BUNDLE_A_SERVICE_NAME)
        registerService(bundleB, BUNDLE_B_SERVICE_ID, BUNDLE_B_SERVICE_NAME)

        addServiceDependency(bundleC, BUNDLE_A_SERVICE_ID, 0)
        addServiceDependency(bundleC, NO_EXISTING_SERVICE_ID, 1)
        addServiceListDependency(bundleC, [BUNDLE_A_SERVICE_ID, NO_EXISTING_SERVICE_ID], 0)

        when:
        List<BundleField> result = bundleUtils.getAllBundleFields()
        Optional<BundleField> bundleCResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_C_NAME) }).findFirst()

        then:
        result.size() == 3
        bundleCResult.isPresent()
        bundleCResult.get().serviceRefs().size() == 1
        bundleCResult.get().serviceRefs().get(0).serviceInterface() == EXAMPLE_INTERFACE
        bundleCResult.get().serviceRefs().get(0).filter() == "(" + BUNDLE_A_SERVICE_ID + ")"
        bundleCResult.get().serviceRefs().get(0).resolution() == ServiceReferenceField.OPTIONAL
        bundleCResult.get().serviceRefs().get(0).service().bundleId() == BUNDLE_A_ID
        bundleCResult.get().serviceRefs().get(0).service().serviceName() == BUNDLE_A_SERVICE_NAME

        bundleCResult.get().serviceRefLists().size() == 1
        bundleCResult.get().serviceRefLists().get(0).services().size() == 1
        bundleCResult.get().serviceRefLists().get(0).referenceListInterface() == EXAMPLE_INTERFACE
        bundleCResult.get().serviceRefLists().get(0).filter() == "(" + [BUNDLE_A_SERVICE_ID, NO_EXISTING_SERVICE_ID].join(",") + ")"
        bundleCResult.get().serviceRefLists().get(0).resolution() == ServiceReferenceField.OPTIONAL
        bundleCResult.get().serviceRefLists().get(0).services().get(0).serviceName() == BUNDLE_A_SERVICE_NAME
        bundleCResult.get().serviceRefLists().get(0).services().get(0).bundleId() == BUNDLE_A_ID
    }

    def "Get only bundles matching ids"() {
        setup:
        addMockBundle(BUNDLE_A_NAME, BUNDLE_A_ID, BUNDLE_A_LOCATION, [], [:])
        addMockBundle(BUNDLE_B_NAME, BUNDLE_B_ID, BUNDLE_B_LOCATION, [], [:])
        addMockBundle(BUNDLE_C_NAME, BUNDLE_C_ID, BUNDLE_C_LOCATION, [], [:])

        when:
        List<BundleField> result = bundleUtils.getBundles([BUNDLE_C_ID])
        Optional<BundleField> bundleCResult = result.stream().filter({ bundle -> (bundle.bundleName() == BUNDLE_C_NAME) }).findFirst()

        then:
        result.size() == 1
        bundleCResult.isPresent()
    }

    Bundle addMockBundle(String symbolicName, long bundleId, String location, List<String> exportedPkgs, Map<String, Bundle> importPkgs) {
        Bundle mockBundle = Mock(Bundle)
        mockBundle.getSymbolicName() >> symbolicName
        mockBundle.getBundleId() >> bundleId
        mockBundle.getLocation() >> location

        mockBundle.getHeaders() >> new MapToDictionary([(BundleUtils.EXPORT_PACKAGE_HEADER): exportedPkgs.join(",")])
        bundleToPkgDepMap.put(mockBundle, importPkgs)
        allMockBundles.add(mockBundle)
        return mockBundle
    }

    void registerService(Bundle bundle, String serviceId, String serviceName) {
        ServiceReference ref = Mock(ServiceReference)
        ref.getBundle() >> bundle
        allServiceRefs.put(serviceId, ref)

        if (bundleToRegisteredServices.containsKey(bundle)) {
            bundleToRegisteredServices.put(bundle, bundleToRegisteredServices.get(bundle) + [ref])
        } else {
            bundleToRegisteredServices.put(bundle, [ref])
        }

        Object mockService = Mock(Object)
        mockService.toString() >> serviceName
        allServices.put(ref, mockService)

        addMetatypeToBlueprintContainer(bundle, serviceId, Mock(ServiceMetadata))
    }

    void addServiceDependency(Bundle bundle, String serviceId, int availability) {
        ReferenceMetadata ref = Mock(ReferenceMetadata)
        ref.getFilter() >> serviceId
        ref.getInterface() >> EXAMPLE_INTERFACE
        ref.getAvailability() >> availability

        addMetatypeToBlueprintContainer(bundle, serviceId, ref)
    }

    void addServiceListDependency(Bundle bundle, List<String> serviceId, int availability) {
        ReferenceListMetadata ref = Mock(ReferenceListMetadata)
        ref.getFilter() >> serviceId.join(",")
        ref.getInterface() >> EXAMPLE_INTERFACE
        ref.getAvailability() >> availability
        addMetatypeToBlueprintContainer(bundle, serviceId.join(","), ref)
    }

    void addMetatypeToBlueprintContainer(Bundle bundle, String serviceId, ComponentMetadata metadata) {
        if (allBlueprintContainers.containsKey(bundle)) {
            allBlueprintContainers.get(bundle).addMetadata(serviceId, metadata)
        } else {
            allBlueprintContainers.put(bundle, new MockBlueprintContainer().addMetadata(serviceId, metadata))
        }
    }

    static class MockBlueprintContainer implements BlueprintContainer {
        private Map<String, ComponentMetadata> metadata = [:]

        @Override
        Set<String> getComponentIds() {
            return metadata.keySet()
        }

        @Override
        ComponentMetadata getComponentMetadata(String id) {
            return metadata.get(id)
        }

        @Override
        Object getComponentInstance(String id) {
            return null
        }

        @Override
        def <T extends ComponentMetadata> Collection<T> getMetadata(Class<T> type) {
            return null
        }

        private MockBlueprintContainer addMetadata(String id, ComponentMetadata dep) {
            metadata.put(id, dep)
            return this
        }
    }
}
