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
