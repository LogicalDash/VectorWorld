/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planargame;

/**
 *
 * @author sanotehu
 */
public class ConfigException extends Exception {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException() {
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
