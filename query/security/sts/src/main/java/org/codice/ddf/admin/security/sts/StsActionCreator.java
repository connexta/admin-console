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
package org.codice.ddf.admin.security.sts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.common.actions.BaseActionCreator;
import org.codice.ddf.admin.security.sts.actions.GetStsClaimsAction;

public class StsActionCreator extends BaseActionCreator {

    public static final String NAME = "sts";
    public static final String TYPE_NAME = "SecurityTokenService";
    public static final String DESCRIPTION = "The STS (Security Token Service) is responsible for generating assertions that allow clients to be authenticated.";

    public StsActionCreator() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return Arrays.asList(new GetStsClaimsAction());
    }

    @Override
    public List<Action> getPersistActions() {
        return new ArrayList<>();
    }
}
