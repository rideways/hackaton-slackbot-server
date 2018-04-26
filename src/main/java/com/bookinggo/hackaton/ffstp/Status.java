package com.bookinggo.hackaton.ffstp;

public enum Status {

    OK,
    ERROR,
    ERROR_INVALID_MESSAGE,
    DIE,
    UNKNOWN;

    public static com.bookinggo.hackaton.ffstp.Status fromString(String status) {
        for (com.bookinggo.hackaton.ffstp.Status s : com.bookinggo.hackaton.ffstp.Status.values()) {
            if (s.name()
                 .equalsIgnoreCase(status)) {
                return s;
            }
        }
        return UNKNOWN;
    }

}
