package com.bookinggo.hackaton.ffstp.exception;

import java.nio.CharBuffer;

public class MissingDataException extends RuntimeException {
    public MissingDataException(String readData) {
        super("Not enough data in the buffer, retrieved string: " + readData);
    }

    public MissingDataException(char[] buffer) {
        super("Not enough data in the buffer, retrieved bytes: " + CharBuffer.wrap(buffer)
                                                                             .chars()
                                                                             .mapToObj(String::valueOf)
                                                                             .reduce((a, b) -> a + " " + b));
    }
}
