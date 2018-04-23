package com.bookinggo.hackaton.domain.slackapp;

import lombok.Getter;

import java.util.Arrays;

@Getter
enum Command {

    ADD("Add new custom script. Example: /superbot add scriptName <code>"),
    RM("Remove script by name. Example: /superbot rm scriptName"),
    RUN("Run command with follow parameters. Example: /superbot run scriptName param1 param2"),
    LIST("List all available scripts"),
    HELP("show available commands");

    private final String desc;

    Command(String desc) {
        this.desc = desc;
    }

    static Command of(String command) {
        for (Command c : Command.values()) {
            if (c.name()
                 .equalsIgnoreCase(command)) {
                return c;
            }
        }
        return HELP;
    }

    static String toHelp() {
        return Arrays.stream(values())
                     .map(s -> s.name()
                                .toLowerCase() + ": " + s.getDesc())
                     .reduce((d1, d2) -> d1 + "\n" + d2)
                     .orElseThrow(() -> new RuntimeException(""));
    }

}
