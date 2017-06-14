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
package org.codice.ddf.admin.ldap.commons;

import java.io.Closeable;
import java.io.IOException;

import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.forgerock.opendj.ldap.Connection;

public class LdapConnectionAttempt extends ReportWithResultImpl<Connection> implements Closeable {

    public LdapConnectionAttempt() {
        super();
    }

    public LdapConnectionAttempt(Connection connection) {
        super(connection);
    }

    @Override
    public LdapConnectionAttempt addArgumentMessage(ErrorMessage message) {
        super.addArgumentMessage(message);
        return this;
    }

    @Override
    public LdapConnectionAttempt addResultMessage(ErrorMessage message) {
        super.addResultMessage(message);
        return this;
    }

    @Override
    public void close() throws IOException {
        if (isResultPresent() && !result().isClosed()) {
            result().close();
        }
    }
}
