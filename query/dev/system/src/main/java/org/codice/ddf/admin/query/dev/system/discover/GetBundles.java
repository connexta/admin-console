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

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils;
import org.codice.ddf.admin.query.dev.system.fields.BundleField;

public class GetBundles extends BaseFunctionField<ListField<BundleField>> {

  public static final String FIELD_NAME = "bundles";

  public static final String DESCRIPTION =
      "Retrieves all bundles in the OSGI environment instance. If bundle id's are specified, only those bundles will be returned.";

  public static final String BUNDLE_IDS = "bundleIds";

  private IntegerField.ListImpl bundleIds;

  private static final BundleField.ListImpl RETURN_TYPE = new BundleField.ListImpl();

  private BundleUtils bundleUtils;

  public GetBundles(BundleUtils bundleUtils) {
    super(FIELD_NAME, DESCRIPTION);
    bundleIds = new IntegerField.ListImpl(BUNDLE_IDS);
    this.bundleUtils = bundleUtils;
  }

  @Override
  public ListField<BundleField> performFunction() {
    if (bundleIds.getList().isEmpty()) {
      return new BundleField.ListImpl().addAll(bundleUtils.getAllBundleFields());
    } else {
      List<BundleField> bundles = bundleUtils.getBundles(bundleIds.getValue());
      return new BundleField.ListImpl().addAll(bundles);
    }
  }

  @Override
  public FunctionField<ListField<BundleField>> newInstance() {
    return new GetBundles(bundleUtils);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return Collections.emptySet();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(bundleIds);
  }

  @Override
  public BundleField.ListImpl getReturnType() {
    return RETURN_TYPE;
  }
}
