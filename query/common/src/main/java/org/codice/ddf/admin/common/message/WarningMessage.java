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
package org.codice.ddf.admin.common.message;

import java.util.List;

public class WarningMessage extends BaseMessage {

    public WarningMessage(String code) {
        super(MessageType.WARNING, code);
    }

    public WarningMessage(String code, String pathOrigin) {
        super(MessageType.WARNING, code, pathOrigin);
    }

    public WarningMessage(String code, List<String> path) {
        super(MessageType.ERROR, code, path);
    }

    @Override
    public WarningMessage copy() {
        return new WarningMessage(getCode(), getPath());
    }
}
