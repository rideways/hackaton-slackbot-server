package com.bookinggo.hackaton.ffstp.exception;

public class InvalidMessageLengthException extends RuntimeException {

    public InvalidMessageLengthException(int messageLength) {
        super("Message length must be >= 0, received '" + messageLength + "'");
    }

    public InvalidMessageLengthException(String messageLength, NumberFormatException cause) {
        super("Message length must be a number, got '" + messageLength + "'", cause);
    }
}
