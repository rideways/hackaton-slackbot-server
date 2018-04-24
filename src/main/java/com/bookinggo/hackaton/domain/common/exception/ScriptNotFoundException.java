package com.bookinggo.hackaton.domain.common.exception;

public class ScriptNotFoundException extends RuntimeException {

    public ScriptNotFoundException(long id) {
        super("Script with ID " + id + " could not be found");
    }
}
