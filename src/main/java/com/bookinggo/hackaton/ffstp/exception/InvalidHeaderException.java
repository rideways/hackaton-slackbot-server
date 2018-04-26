package com.bookinggo.hackaton.ffstp.exception;

public class InvalidHeaderException extends RuntimeException {
    public InvalidHeaderException(String actualHeader) {
        super("Message header is invalid: '" + actualHeader + "'");
    }
}
