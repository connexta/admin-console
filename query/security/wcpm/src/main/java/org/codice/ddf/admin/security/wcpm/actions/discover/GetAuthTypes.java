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
package org.codice.ddf.admin.security.wcpm.actions.discover;

import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.security.common.fields.wcpm.AuthTypeList;
import org.codice.ddf.admin.security.wcpm.sample.SampleFields;

public class GetAuthTypes extends GetAction<AuthTypeList> {

    public static final String FIELD_NAME = "authTypes";

    public static final String DESCRIPTION =
            "Retrieves all currently configured authentication types.";

    public GetAuthTypes() {
        super(FIELD_NAME, DESCRIPTION, new AuthTypeList());
    }

    @Override
    public AuthTypeList performAction() {
        return SampleFields.SAMPLE_AUTH_TYPES_LIST;
    }
}
