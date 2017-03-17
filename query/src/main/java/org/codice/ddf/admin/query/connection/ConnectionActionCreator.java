package org.codice.ddf.admin.query.connection;

import java.util.List;

import org.codice.ddf.admin.query.api.action.Action;
import org.codice.ddf.admin.query.commons.actions.BaseActionCreator;
import org.codice.ddf.admin.query.connection.actions.PingByAddress;
import org.codice.ddf.admin.query.connection.actions.PingByUrl;

import com.google.common.collect.ImmutableList;

public class ConnectionActionCreator extends BaseActionCreator {

    public static final String NAME = "conn";
    public static final String TYPE_NAME = "Connection";
    public static final String DESCRIPTION = "Provides actions for connecting to urls.";

    public ConnectionActionCreator() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new PingByAddress(), new PingByUrl());
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of();
    }
}