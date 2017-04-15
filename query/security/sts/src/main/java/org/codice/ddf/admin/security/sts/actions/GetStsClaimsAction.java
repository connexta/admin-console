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
package org.codice.ddf.admin.security.sts.actions;

import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.security.common.fields.sts.StsClaimField;

public class GetStsClaimsAction extends GetAction<ListFieldImpl<StsClaimField>> {

    public static final String NAME = "claims";

    public static final String DESCRIPTION = "All currently configured claims the STS supports.";

    public GetStsClaimsAction() {
        super(NAME, DESCRIPTION, new ListFieldImpl<>(StsClaimField.class));
    }

    @Override
    public ListFieldImpl performAction() {
        StsClaimField claim = new StsClaimField();
        claim.setValue("testClaim");
        return new ListFieldImpl<>(StsClaimField.class).add(claim);
    }
}
