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

import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.security.common.fields.wcpm.Realm;

public class GetRealms extends GetAction<ListField<Realm>> {

    public static final String FIELD_NAME = "realms";

    public static final String DESCRIPTION = "Retrieves all currently configured realms.";

    public GetRealms() {
        super(FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(Realm.class));
    }

    @Override
    public ListField<Realm> performAction() {
        // TODO: 3/31/17 Make reference to the ldap action creator once it is implemented clear.
        return new ListFieldImpl<>(Realm.class).add(Realm.KARAF_REALM)
                .add(Realm.LDAP_REALM);
    }
}
