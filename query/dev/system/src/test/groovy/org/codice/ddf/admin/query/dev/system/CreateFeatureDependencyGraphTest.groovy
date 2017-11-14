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

import org.apache.karaf.features.Feature
import org.codice.ddf.admin.api.report.Report
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.query.dev.system.dependency.FeatureUtils
import org.codice.ddf.admin.query.dev.system.fields.FeatureField
import org.codice.ddf.admin.query.dev.system.persist.CreateFeatureDependencyGraph
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Paths
import java.security.Permission

class CreateFeatureDependencyGraphTest extends Specification {

    private SampleData sampleData = new SampleData()
    private FeatureUtils featureUtils = Mock(FeatureUtils)
    private CreateFeatureDependencyGraph function = new CreateFeatureDependencyGraph(featureUtils)
    private List<String> FUNCTION_PATH = [CreateFeatureDependencyGraph.FUNCTION_NAME]

    @Rule
    TemporaryFolder temporaryFolder
    private File defaultSavePath
    private File tempSaveDir

    def setup() {
        featureUtils.getAllFeatures() >> [SampleData.featureA(), SampleData.featureB(), SampleData.featureC()]
        featureUtils.getFeature(_) >> { String featName -> matchFeature(featName).isPresent() ? matchFeature(featName).get() : null }
        tempSaveDir = temporaryFolder.newFolder()
        defaultSavePath = Paths.get(tempSaveDir.getAbsolutePath(), CreateFeatureDependencyGraph.DEFAULT_GRAPH_NAME).toFile()
        System.setProperty("ddf.home", tempSaveDir.getAbsolutePath())
    }

    Optional<Feature> matchFeature(String featName) {
        Optional<FeatureField> match = featureUtils.getAllFeatures().stream().filter({ feat -> feat.id() == featName }).findFirst()
        return match.isPresent() ? Optional.of(sampleData.featureFieldToFeature(match.get())) : Optional.empty()
    }

    def "Create graph successfully"() {
        when:
        Report<Boolean> report = function.execute(null, FUNCTION_PATH)

        then:
        !report.containsErrorMessages()
        report.getResult()
        defaultSavePath.exists()
    }

    def "Error returned when graph creation fails"() {
        setup:
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

    def "Skip edges with features that can't be found"() {
        setup:
        featureUtils.getFeature(_) >> null
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


    def "Validate arguments correctly"() {
        when:
        Report<Boolean> report = function.execute([(CreateFeatureDependencyGraph.SAVE_DIR): Paths.get("does", "not", "exist").toString()], FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().get(0).getCode() == DefaultMessages.DIRECTORY_DOES_NOT_EXIST
        report.getErrorMessages().get(0).getPath() == FUNCTION_PATH + CreateFeatureDependencyGraph.SAVE_DIR
    }

    def "Use save dir when specified"() {
        setup:
        def newSaveDir = temporaryFolder.newFolder()
        def newGraphLocation = Paths.get(newSaveDir.getAbsolutePath(), CreateFeatureDependencyGraph.DEFAULT_GRAPH_NAME).toFile()

        when:
        Report<Boolean> report = function.execute([(CreateFeatureDependencyGraph.SAVE_DIR): newSaveDir.getAbsolutePath()], FUNCTION_PATH)

        then:
        !report.containsErrorMessages()
        report.getResult()
        newGraphLocation.exists()
    }
}
