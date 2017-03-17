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
package org.codice.ddf.admin.query.commons.actions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.query.api.action.Action;
import org.codice.ddf.admin.query.api.action.ActionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseActionCreator implements ActionCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseActionCreator.class);

    private String name;
    private String typeName;
    private String description;

    public BaseActionCreator(String name, String typeName, String description) {
        this.name = name;
        this.typeName = typeName;
        this.description = description;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Action createAction(String actionId) {
        Optional<Action> foundAction = Stream.concat(getDiscoveryActions().stream(),
                getPersistActions().stream())
                .filter(action -> action.name().equals(actionId))
                .findFirst();

        if (foundAction.isPresent()) {
            return foundAction.get();
        }

        List<String> allActionIds = Stream.concat(getDiscoveryActions().stream(),
                getPersistActions().stream())
                .map(Action::name)
                .collect(Collectors.toList());

        LOGGER.debug("Unknown actionId {} for handler {}. Known action id's are [{}]", actionId, name, String.join(",", allActionIds));
        return null;
    }
}
