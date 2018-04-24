package com.bookinggo.hackaton.domain.process;

class JvmArgumentsConcatenationException extends RuntimeException {
    JvmArgumentsConcatenationException() {
        super("JVM arguments could not be added, please add at least one custom (-D) argument to the main process");
    }
}
