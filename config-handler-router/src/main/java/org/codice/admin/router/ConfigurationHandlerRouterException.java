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
 */
package org.codice.admin.router;

/**
 * The {@code ConfigurationHandlerRouterException} will be thrown when an unknown {@link Throwable} has occurred
 * that is unable to be handled up by the {@link ConfigurationHandlerRouter}.
 */
public class ConfigurationHandlerRouterException extends Exception {

    /**
     * Instantiates a new ConfigurationHandlerRouterException from a given string.
     *
     * @param string
     *            the string to use for the exception.
     */
    public ConfigurationHandlerRouterException(String string) {
        super(string);
    }

    /**
     * Instantiates a new PluginExecutionException.
     */
    public ConfigurationHandlerRouterException() {
        super();
    }

    /**
     * Instantiates a new ConfigurationHandlerRouterException with a message.
     *
     * @param message
     *            the message
     * @param throwable
     *            the throwable
     */
    public ConfigurationHandlerRouterException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Instantiates a new ConfigurationHandlerRouterException.
     *
     * @param throwable
     *            the throwable
     */
    public ConfigurationHandlerRouterException(Throwable throwable) {
        super(throwable);
    }
}
