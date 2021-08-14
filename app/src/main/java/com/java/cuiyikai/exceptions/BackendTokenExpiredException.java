package com.java.cuiyikai.exceptions;

public class BackendTokenExpiredException extends Exception {
    public BackendTokenExpiredException() {
        super();
    }

    public BackendTokenExpiredException(String message) {
        super(message);
    }

    public BackendTokenExpiredException(Throwable cause) {
        super(cause);
    }

    public BackendTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}