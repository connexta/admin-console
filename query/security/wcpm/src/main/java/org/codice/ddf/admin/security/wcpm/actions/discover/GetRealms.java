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

import static org.codice.ddf.admin.security.wcpm.sample.SampleFields.SAMPLE_REALM_LIST;

import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.security.common.fields.wcpm.RealmList;

public class GetRealms extends GetAction<RealmList> {

    public static final String FIELD_NAME = "realms";

    public static final String DESCRIPTION = "Retrieves all currently configured realms.";

    public GetRealms() {
        super(FIELD_NAME, DESCRIPTION, new RealmList());
    }

    @Override
    public RealmList performAction() {
        return SAMPLE_REALM_LIST;
    }
}
