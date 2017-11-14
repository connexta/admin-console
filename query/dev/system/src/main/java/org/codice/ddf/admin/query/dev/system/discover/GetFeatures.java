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
package org.codice.ddf.admin.query.dev.system.discover;

import java.util.Collections;
import java.util.Set;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.query.dev.system.dependency.FeatureUtils;
import org.codice.ddf.admin.query.dev.system.fields.FeatureField;

public class GetFeatures extends GetFunctionField<ListField<FeatureField>> {

  public static final String FIELD_NAME = "features";
  public static final String DESCRIPTION =
      "Retrieves all features in the currently running Karaf instance.";

  public static final FeatureField.ListImpl RETURN_TYPE = new FeatureField.ListImpl();

  private FeatureUtils featureUtils;

  public GetFeatures(FeatureUtils featureUtils) {
    super(FIELD_NAME, DESCRIPTION);
    this.featureUtils = featureUtils;
  }

  @Override
  public ListField<FeatureField> performFunction() {
    return new FeatureField.ListImpl().addAll(featureUtils.getAllFeatures());
  }

  @Override
  public ListField<FeatureField> getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField newInstance() {
    return new GetFeatures(featureUtils);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }
}
