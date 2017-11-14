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
package org.codice.ddf.admin.security.common;

import static org.codice.ddf.admin.security.common.SecurityMessages.invalidClaimType;

import java.util.List;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.Reports;
import org.codice.ddf.admin.security.common.services.StsServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

public class SecurityValidation {

  private SecurityValidation() {}

  public static Report validateStsClaimsExist(
      List<StringField> claimArgs,
      ServiceActions serviceActions,
      StsServiceProperties stsServiceProps) {
    Report report = Reports.emptyReport();
    List<String> supportedClaims = stsServiceProps.getConfiguredStsClaims(serviceActions);

    claimArgs
        .stream()
        .filter(claimArg -> !supportedClaims.contains(claimArg.getValue()))
        .forEach(claimArg -> report.addErrorMessage(invalidClaimType(claimArg.getPath())));

    return report;
  }
}
