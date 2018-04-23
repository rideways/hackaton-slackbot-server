package com.bookinggo.hackaton.domain.common.exception;

public class ResourceAccessException extends RuntimeException {
    public ResourceAccessException(String resource) {
        super("resounce " + resource + " cannot be accessed from the current context");
    }
}
