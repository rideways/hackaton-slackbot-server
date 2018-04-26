package com.bookinggo.hackaton.ffstp.exception;

public class ProtocolWriterInitializationException extends ProtocolInitializationException {
    public ProtocolWriterInitializationException(Throwable cause) {
        super("writer stream", cause);
    }
}
