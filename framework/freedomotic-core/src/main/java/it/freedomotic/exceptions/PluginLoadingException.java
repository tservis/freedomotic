/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.freedomotic.exceptions;

import java.util.logging.Logger;

/**
 *
 * @author enrico
 */
public class PluginLoadingException extends FreedomoticException {

    String pluginName;

    /**
     * Creates a new instance of
     * <code>PluginLoadingException</code> without detail message.
     */
    public PluginLoadingException() {
    }

    public String getPluginName() {
        return pluginName;
    }

    public final void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Constructs an instance of
     * <code>PluginLoadingException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public PluginLoadingException(String msg) {
        super(msg);
    }

    public PluginLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PluginLoadingException(String message, String pluginName, Throwable cause) {
        super(message, cause);
        setPluginName(pluginName);
    }

    public PluginLoadingException(Throwable cause) {
        super(cause);
    }
}
