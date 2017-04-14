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
package org.codice.ddf.admin.beta.enforcer;

import static org.codice.ddf.admin.beta.enforcer.ArtifactSizeEnforcerRule.PROJECT_ARTIFACT_ID_PROP;
import static org.codice.ddf.admin.beta.enforcer.ArtifactSizeEnforcerRule.PROJECT_BUILD_DIR_PROP;
import static org.codice.ddf.admin.beta.enforcer.ArtifactSizeEnforcerRule.PROJECT_PACKAGING_PROP;
import static org.codice.ddf.admin.beta.enforcer.ArtifactSizeEnforcerRule.PROJECT_VERSION_PROP;
import static org.codice.ddf.admin.beta.enforcer.ArtifactSizeEnforcerRule.SUPPORTED_PACKAGE_TYPES;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ArtifactSizeEnforcerRuleTest {

    private static final String SAMPLE_ARTIFACT_ID = "SampleArtifactId";
    private static final String SAMPLE_VERSION = "SampleVersion";
    private static final String SAMPLE_ARTIFACT_FILE_NAME = SAMPLE_ARTIFACT_ID + "-" + SAMPLE_VERSION + ".jar";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private File sampleBuildDir;
    private MockEnforcementRuleHelper defaultMockhelper;

    @Before
    public void before() throws IOException {
        sampleBuildDir = tempFolder.getRoot();
        defaultMockhelper = new MockEnforcementRuleHelper().artifactId(SAMPLE_ARTIFACT_ID)
                .packaging(ArtifactSizeEnforcerRule.JAR)
                .version(SAMPLE_VERSION)
                .buildDir(sampleBuildDir.getPath());
    }
    @Test
    public void unknownPackaging() {
        ArtifactSizeEnforcerRule enforcer = new ArtifactSizeEnforcerRule();
        defaultMockhelper.packaging("UnknownPackageType");
        try {
            String packaging = enforcer.getPackaging(defaultMockhelper);
            assertTrue(!SUPPORTED_PACKAGE_TYPES.contains(packaging));
        } catch (EnforcerRuleException e) {
            fail("ArtifactSizerEnforcerRule was unable to skip an unknown packing type.");
        }
    }

    @Test
    public void specifiedArtifactExists() throws IOException {
        String sampleArtifactPath = tempFolder.newFile("SampleArtifact.jar").getPath();
        ArtifactSizeEnforcerRule enforcer = new ArtifactSizeEnforcerRule().setArtifactLocation(sampleArtifactPath);
        try {
            enforcer.execute(defaultMockhelper);
        } catch (EnforcerRuleException e) {
            fail("Unable to find specified artifact.");
        }
    }

    @Test(expected=EnforcerRuleException.class)
    public void specifiedArtifactDoesNotExist() throws EnforcerRuleException {
        ArtifactSizeEnforcerRule enforcer = new ArtifactSizeEnforcerRule().setArtifactLocation("ArtifactDoesNotExist.jar");
        enforcer.execute(defaultMockhelper);
    }

    @Test
    public void successfullyDiscoverArtifact() throws IOException {
        tempFolder.newFile(SAMPLE_ARTIFACT_FILE_NAME);
        ArtifactSizeEnforcerRule enforcer =  new ArtifactSizeEnforcerRule();
        try {
            enforcer.execute(defaultMockhelper);
        } catch (EnforcerRuleException e) {
            fail("Failed to discover artifact.");
        }
    }

    @Test(expected=EnforcerRuleException.class)
    public void failToDiscoverArtifact() throws EnforcerRuleException {
        ArtifactSizeEnforcerRule enforcer =  new ArtifactSizeEnforcerRule();
        enforcer.execute(defaultMockhelper);
    }

    @Test(expected=EnforcerRuleException.class)
    public void artifactSizeAboveMax() throws EnforcerRuleException, IOException {
        tempFolder.newFile(SAMPLE_ARTIFACT_FILE_NAME);
        ArtifactSizeEnforcerRule enforcer = new ArtifactSizeEnforcerRule().setMaxArtifactSize("-1_MB");
        enforcer.execute(defaultMockhelper);
    }

    @Test (expected=EnforcerRuleException.class)
    public void invalidArtifactSizeUnit() throws IOException, EnforcerRuleException {
        tempFolder.newFile(SAMPLE_ARTIFACT_FILE_NAME);
        ArtifactSizeEnforcerRule enforcer = new ArtifactSizeEnforcerRule();
        enforcer.setMaxArtifactSize("INVALID_UNIT");
        enforcer.execute(defaultMockhelper);
    }
    @Test
    public void testByteConversion() throws EnforcerRuleException {
        ArtifactSizeEnforcerRule enforcer =  new ArtifactSizeEnforcerRule();
        enforcer.setMaxArtifactSize("1024_B");
        assertEquals(enforcer.maxArtifactSizeToBytes(defaultMockhelper), 1024);

        enforcer.setMaxArtifactSize("1_KB");
        assertEquals(enforcer.maxArtifactSizeToBytes(defaultMockhelper), 1024);

        enforcer.setMaxArtifactSize("1_MB");
        assertEquals(enforcer.maxArtifactSizeToBytes(defaultMockhelper), 1024 * 1024);
    }

    public class MockEnforcementRuleHelper implements EnforcerRuleHelper {

        private String packaging;
        private String artifactId;
        private String version;
        private String buildDir;

        @Override
        public Log getLog() {
            return new SystemStreamLog();
        }

        @Override
        public Object getComponent(Class clazz) throws ComponentLookupException {
            return null;
        }

        @Override
        public Object getComponent(String componentKey) throws ComponentLookupException {
            return null;
        }

        @Override
        public Object getComponent(String role, String roleHint) throws ComponentLookupException {
            return null;
        }

        @Override
        public Map getComponentMap(String role) throws ComponentLookupException {
            return null;
        }

        @Override
        public List getComponentList(String role) throws ComponentLookupException {
            return null;
        }

        @Override
        public PlexusContainer getContainer() {
            return null;
        }

        @Override
        public Object evaluate(String s) throws ExpressionEvaluationException {
            switch (s) {
            case PROJECT_PACKAGING_PROP:
                return packaging;
            case PROJECT_ARTIFACT_ID_PROP:
                return artifactId;
            case PROJECT_VERSION_PROP:
                return version;
            case PROJECT_BUILD_DIR_PROP:
                return buildDir;
            default:
                fail("Unknown evaluate arg: " + s);
                return null;
            }
        }

        @Override
        public File alignToBaseDirectory(File file) {
            return null;
        }

        public MockEnforcementRuleHelper packaging(String packaging) {
            this.packaging = packaging;
            return this;
        }

        public MockEnforcementRuleHelper artifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public MockEnforcementRuleHelper version(String version) {
            this.version = version;
            return this;
        }

        public MockEnforcementRuleHelper buildDir(String buildDir) {
            this.buildDir = buildDir;
            return this;

        }
    }
}
