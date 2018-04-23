package com.bookinggo.hackaton.domain.script.dto;

import lombok.Value;

@Value
public class ScriptDto {

    private final String name;
    private final String contents;
    private final String language;
    private final String ownerUsername;

}
