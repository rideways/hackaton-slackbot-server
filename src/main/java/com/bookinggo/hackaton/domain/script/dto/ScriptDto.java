package com.bookinggo.hackaton.domain.script.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ScriptDto {

    private final String name;
    private final String contents;
    private final String language;
    private final String ownerUsername;
    private final String ownerSlackUserId;

}
