package com.bookinggo.hackaton.ffstp.exception;

public class RethrownException extends RuntimeException {
    public RethrownException(Throwable throwable) {
        super("A checked exception was rethrown, see cause for more details", throwable);
    }
}
