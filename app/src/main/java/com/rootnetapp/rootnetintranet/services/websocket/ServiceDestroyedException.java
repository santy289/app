package com.rootnetapp.rootnetintranet.services.websocket;

/**
 * The ServiceDestroyedException should be used when the push notifications web sockets service is destroyed
 **/
public class ServiceDestroyedException extends Exception {

    public ServiceDestroyedException(String message) {
        super(message);
    }

    public ServiceDestroyedException(String message, Throwable cause) {
        super(message, cause);
    }
}
