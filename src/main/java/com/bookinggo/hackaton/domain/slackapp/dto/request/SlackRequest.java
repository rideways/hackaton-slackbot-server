package com.bookinggo.hackaton.domain.slackapp.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SlackRequest {

    private final String teamDomain;
    private final String channelId;
    private final String channelName;
    private final String userId;
    private final String userName;
    private final String command;
    private final String text;
    private final String responseUrl;
    private final String triggerId;

    public static SlackRequestBuilder builder(MultiValueMap<String, List<String>> request) {
        return new SlackRequestBuilder(request);
    }

    public static class SlackRequestBuilder {
        private final MultiValueMap<String, List<String>> request;

        public SlackRequestBuilder(MultiValueMap<String, List<String>> request) {
            this.request = request;
        }

        public SlackRequest build() {
            return new SlackRequest(
                    getValue("team_domain"),
                    getValue("channel_id"),
                    getValue("channel_name"),
                    getValue("user_id"),
                    getValue("user_name"),
                    getValue("command"),
                    getValue("text"),
                    getValue("response_url"),
                    getValue("trigger_id")
            );
        }

        private String getValue(String key) {
            return String.valueOf(request.get(key)
                                         .get(0));
        }
    }
}
