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
package org.codice.admin.module;

import java.net.URI;
import java.net.URISyntaxException;
import org.codice.ddf.ui.admin.api.module.AdminModule;

public class AdminConfModule implements AdminModule {
  private final String name;
  private final String id;
  private final String iFrameLocation;

  public AdminConfModule(String name, String id, String iFrameLocation) {
    this.name = name;
    this.id = id;
    this.iFrameLocation = iFrameLocation;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public URI getJSLocation() {
    return null;
  }

  @Override
  public URI getCSSLocation() {
    return null;
  }

  @Override
  public URI getIframeLocation() {
    try {
      return new URI(iFrameLocation);
    } catch (URISyntaxException e) {
      return null;
    }
  }
}
