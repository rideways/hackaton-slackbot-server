package com.bookinggo.hackaton.ffstp.exception;

public class ProtocolReaderInitializationException extends com.bookinggo.hackaton.ffstp.exception.ProtocolInitializationException {
    public ProtocolReaderInitializationException(Throwable cause) {
        super("reader stream", cause);
    }
}
