package com.bookinggo.hackaton.ffstp;

import com.bookinggo.hackaton.ffstp.Status;

public class Message<T> {

    private final T data;
    private final String status;

    public static <T> com.bookinggo.hackaton.ffstp.Message<T> empty() {
        return new com.bookinggo.hackaton.ffstp.Message<>((String) null, null);
    }

    public Message(Status status, T data) {
        this.status = status.name();
        this.data = data;
    }

    public Message(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public Status getStatusAsEnum() {
        return Status.fromString(status);
    }

    @Override
    public String toString() {
        return "Message(" + String.valueOf(data)
                                  .length() + ")[" + status + ";" + data + "]";
    }
}
