package org.codice.ddf.admin.query.connection;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.commons.BaseActionHandler;
import org.codice.ddf.admin.query.connection.actions.PingByAddress;
import org.codice.ddf.admin.query.connection.actions.PingByUrl;

import com.google.common.collect.ImmutableList;

public class ConnectionActionHandler extends BaseActionHandler {

    public static final String FIELD_NAME = "conn";
    public static final String FIELD_TYPE_NAME = "Connection";
    public static final String DESCRIPTION = "Provides actions for connecting to urls.";

    public ConnectionActionHandler() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<ActionField> getDiscoveryActions() {
        return ImmutableList.of(new PingByAddress(), new PingByUrl());
    }

    @Override
    public List<ActionField> getPersistActions() {
        return ImmutableList.of();
    }
}