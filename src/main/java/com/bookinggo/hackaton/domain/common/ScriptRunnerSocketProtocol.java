package com.bookinggo.hackaton.domain.common;

public class ScriptRunnerSocketProtocol {

    public enum ParentMessage {
        SCRIPT_BEGIN,
        SCRIPT_END,
        SCRIPT_RUN_START,
        SCRIPT_RUN_END,
        DIE
    }

    public enum ChildMessage {
        OK,
        ERROR_LANGUAGE_NOT_RECOGNIZED,
        ERROR_UNKNOWN
    }

}
