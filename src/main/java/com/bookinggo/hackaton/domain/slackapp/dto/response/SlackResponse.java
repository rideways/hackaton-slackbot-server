package com.bookinggo.hackaton.domain.slackapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SlackResponse {

    private final String text;
    private final List<String> attachments;
}
