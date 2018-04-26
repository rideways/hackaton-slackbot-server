package com.bookinggo.hackaton.domain.common;

import com.bookinggo.hackaton.ffstp.Message;

public enum  ScriptRunnerSocketProtocolStatus {

    LANGUAGE,
    SCRIPT,
    EXECUTE;

    public boolean in(Message<String> message) {
        return name().equalsIgnoreCase(message.getStatus());
    }

}
