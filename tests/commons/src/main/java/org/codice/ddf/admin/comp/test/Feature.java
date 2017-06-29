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

import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public abstract class Feature {

    private String featureName;

    private boolean bootFeature;

    public Feature(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Feature bootFeature(boolean bootFeature) {
        this.bootFeature = bootFeature;
        return this;
    }

    public boolean isBootFeature() {
        return bootFeature;
    }

    public abstract MavenArtifactUrlReference getUrl();
}
