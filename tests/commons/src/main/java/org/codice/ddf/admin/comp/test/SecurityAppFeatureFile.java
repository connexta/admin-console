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
package org.codice.ddf.admin.comp.test;

import static org.ops4j.pax.exam.CoreOptions.maven;

import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public class SecurityAppFeatureFile extends Feature {

    private static final String SECURITY_ALL = "security-securityAll-fix";

    protected SecurityAppFeatureFile(String featureName) {
        super(featureName);
    }

    public static SecurityAppFeatureFile featureFile() {
        return new SecurityAppFeatureFile(null);
    }

    public Feature securityAll() {
        return new SecurityAppFeatureFile(SECURITY_ALL);
    }

    @Override
    public MavenArtifactUrlReference getUrl() {
        return maven().groupId(
                "ddf.security")
                .artifactId("security-services-app")
                .versionAsInProject()
                .type("xml")
                .classifier("features");
    }
}
