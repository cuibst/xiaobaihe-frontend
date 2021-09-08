package com.java.cuiyikai.exceptions;

/**
 * This {@link Exception} is for the case of request token expired
 */
public class BackendTokenExpiredException extends Exception {
    public BackendTokenExpiredException(String message) {
        super(message);
    }
}