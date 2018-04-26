package com.bookinggo.hackaton.ffstp.exception;

import static java.util.Objects.*;

class ProtocolInitializationException extends RuntimeException {
    ProtocolInitializationException(String notInitializedPart, Throwable cause) {
        super("Protocol " + notInitializedPart + " could not be initialized", requireNonNull(cause));
    }
}
