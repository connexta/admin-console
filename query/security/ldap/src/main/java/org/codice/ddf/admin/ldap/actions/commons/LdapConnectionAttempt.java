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
package org.codice.ddf.admin.ldap.actions.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codice.ddf.admin.api.action.Message;
import org.forgerock.opendj.ldap.Connection;

public class LdapConnectionAttempt {

    private List<Message> msgs;
    private Optional<Connection> connection;

    public LdapConnectionAttempt() {
        this.msgs = new ArrayList<>();
        connection = Optional.empty();
    }

    public LdapConnectionAttempt(Message msg) {
        this();
        msgs.add(msg);
    }

    public LdapConnectionAttempt(Connection connection) {
        this();
        this.connection = Optional.of(connection);
    }

    public List<Message> messages() {
        return msgs;
    }

    public Optional<Connection> connection() {
        return connection;
    }
}
