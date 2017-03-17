package org.codice.ddf.admin.query.commons.actions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.query.api.action.ActionCreator;
import org.codice.ddf.admin.query.api.action.Action;
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
