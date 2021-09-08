package com.java.cuiyikai.exceptions;

/**
 * This {@link RuntimeException} is for the case of failed to login
 */
public class AuthorizeFaliedException extends RuntimeException {
    public AuthorizeFaliedException(String message) {
        super(message);
    }
}
