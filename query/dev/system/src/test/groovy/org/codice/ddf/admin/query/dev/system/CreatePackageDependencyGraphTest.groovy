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
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils
import org.codice.ddf.admin.query.dev.system.persist.CreatePackageDependencyGraph
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Paths
import java.security.Permission

class CreatePackageDependencyGraphTest extends Specification {

    private BundleUtils bundleUtils = Mock(BundleUtils)
    private CreatePackageDependencyGraph function = new CreatePackageDependencyGraph(bundleUtils)
    private List<String> FUNCTION_PATH = [CreatePackageDependencyGraph.FUNCTION_NAME]

    @Rule
    TemporaryFolder temporaryFolder
    private File defaultSavePath
    private File tempSaveDir

    def setup() {
        tempSaveDir = temporaryFolder.newFolder()
        defaultSavePath = Paths.get(tempSaveDir.getAbsolutePath(), CreatePackageDependencyGraph.DEFAULT_GRAPH_NAME).toFile()
        System.setProperty("ddf.home", tempSaveDir.getAbsolutePath())
    }

    def "Create graph successfully"() {
        setup:
        bundleUtils.getAllBundleFields() >> [SampleData.bundleA(), SampleData.bundleB(), SampleData.bundleC()]

        when:
        Report<Boolean> report = function.execute(null, FUNCTION_PATH)

        then:
        !report.containsErrorMessages()
        report.getResult()
        defaultSavePath.exists()
    }

    def "Graph generates with bundles that can't be found"() {
        def bundleContainingUnknownId = SampleData.bundleB()
        bundleContainingUnknownId.importedPackages().forEach({ pkg -> pkg.bundleId(-1) })
        bundleUtils.getAllBundleFields() >> [SampleData.bundleA(), SampleData.bundleB(), SampleData.bundleC(), bundleContainingUnknownId]

        when:
        Report<Boolean> report = function.execute(null, FUNCTION_PATH)

        then:
        !report.containsErrorMessages()
        report.getResult()
        defaultSavePath.exists()
    }

    def "Defined error codes are returned correctly"() {
        when:
        Set<String> errorCodes = function.getFunctionErrorCodes()

        then:
        errorCodes == [DefaultMessages.DIRECTORY_DOES_NOT_EXIST, DefaultMessages.FAILED_PERSIST] as Set
    }

    def "Error returned when graph creation fails"() {
        setup:
        bundleUtils.getAllBundleFields() >> [SampleData.bundleA(), SampleData.bundleB(), SampleData.bundleC()]
        System.setSecurityManager(new SecurityManager() {

            @Override
            void checkWrite(String fd) {
                throw new SecurityException()
            }

            @Override
            void checkPermission(Permission perm) {
                return
            }
        })

        when:
        Report<Boolean> report = function.execute(null, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == DefaultMessages.FAILED_PERSIST
        report.getErrorMessages().get(0).getPath() == FUNCTION_PATH

        cleanup:
        System.setSecurityManager(null)
    }

    def "Validate arguments correctly"() {
        when:
        Report<Boolean> report = function.execute([(CreatePackageDependencyGraph.SAVE_DIR): Paths.get("does", "not", "exist").toString()], FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == DefaultMessages.DIRECTORY_DOES_NOT_EXIST
        report.getErrorMessages().get(0).getPath() == FUNCTION_PATH + CreatePackageDependencyGraph.SAVE_DIR
    }

    def "Use save dir when specified"() {
        setup:
        bundleUtils.getAllBundleFields() >> [SampleData.bundleA(), SampleData.bundleB(), SampleData.bundleC()]
        def newSaveDir = temporaryFolder.newFolder()
        def newGraphLocation = Paths.get(newSaveDir.getAbsolutePath(), CreatePackageDependencyGraph.DEFAULT_GRAPH_NAME).toFile()

        when:
        Report<Boolean> report = function.execute([(CreatePackageDependencyGraph.SAVE_DIR): newSaveDir.getAbsolutePath()], FUNCTION_PATH)

        then:
        !report.containsErrorMessages()
        report.getResult()
        newGraphLocation.exists()
    }
}
