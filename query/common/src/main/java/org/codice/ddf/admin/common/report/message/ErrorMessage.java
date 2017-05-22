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
package org.codice.ddf.admin.common.report.message;

import java.util.List;

public class ErrorMessage extends BaseMessage {

    public ErrorMessage(String code) {
        super(MessageType.ERROR, code);
    }

    public ErrorMessage(String code, String pathOrigin) {
        super(MessageType.ERROR, code, pathOrigin);
    }

    public ErrorMessage(String code, List<String> path) {
        super(MessageType.ERROR, code, path);
    }

    @Override
    public ErrorMessage copy() {
        return new ErrorMessage(getCode(), getPath());
    }
}
